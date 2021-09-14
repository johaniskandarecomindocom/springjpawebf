package com.ecomindo.onboarding.springjpawebf.data.jpa.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousByteChannel;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.StandardOpenOption;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;

import com.ecomindo.onboarding.springjpawebf.config.Config;
import com.ecomindo.onboarding.springjpawebf.data.jpa.dto.ResponseDTO;
import com.ecomindo.onboarding.springjpawebf.util.SftpUtil;

import reactor.core.publisher.Mono;

@Service
public class FileServiceImpl implements FileService {

	@Autowired
	Config config;

	@Override
	public Mono<ResponseDTO> upload(FilePart filePart) throws Exception {
		ResponseDTO response = new ResponseDTO();
		try {
			SftpUtil sftp = new SftpUtil(config.getSftpHost(), config.getSftpPort(), config.getSftpUsername(),
					config.getSftpPassword(), config.getSftpFolder());

	        System.out.printf("handling file upload {}\n", filePart.filename());
	        // if a file with the same name already exists in a repository, delete and recreate it
	        final String filename = filePart.filename();
	        final String localFilename = UUID.randomUUID().toString().concat("-").concat(filename);
	        String localServerFilepath = config.getSftpServerfolder().concat("/").concat(localFilename);
	        File file = new File(localServerFilepath);
	        if (file.exists())
	            file.delete();
	        try {
	            file.createNewFile();
	        } catch (IOException e) {
	            return Mono.error(e); // if creating a new file fails return an error
	        }

	        try {
	            // create an async file channel to store the file on disk
	            final AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(file.toPath(), StandardOpenOption.WRITE);
	            
	            final CloseCondition closeCondition = new CloseCondition();
	            final CloseCondition transferCondition = new CloseCondition();
	            
	            // for sftp checking
	            transferCondition.onTaskSubmitted();
	            
	            // pointer to the end of file offset
	            AtomicInteger fileWriteOffset = new AtomicInteger(0);
	            // error signal
	            AtomicBoolean errorFlag = new AtomicBoolean(false);

	            System.out.println("subscribing to file parts");
	            // FilePart.content produces a flux of data buffers, each need to be written to the file
	            return filePart.content().doOnEach(dataBufferSignal -> {
	                if (dataBufferSignal.hasValue() && !errorFlag.get()) {
	                    // read data from the incoming data buffer into a file array
	                    DataBuffer dataBuffer = dataBufferSignal.get();
	                    int count = dataBuffer.readableByteCount();
	                    byte[] bytes = new byte[count];
	                    dataBuffer.read(bytes);

	                    // create a file channel compatible byte buffer
	                    final ByteBuffer byteBuffer = ByteBuffer.allocate(count);
	                    byteBuffer.put(bytes);
	                    byteBuffer.flip();

	                    // get the current write offset and increment by the buffer size
	                    final int filePartOffset = fileWriteOffset.getAndAdd(count);
	                    System.out.printf("processing file part at offset {}\n", filePartOffset);
	                    // write the buffer to disk
	                    closeCondition.onTaskSubmitted();
	                    fileChannel.write(byteBuffer, filePartOffset, null, new CompletionHandler<Integer, ByteBuffer>() {
	                        @Override
	                        public void completed(Integer result, ByteBuffer attachment) {
	                            // file part successfuly written to disk, clean up
	                        	System.out.printf("done saving file part {}\n", filePartOffset);
	                            byteBuffer.clear();

	                            if (closeCondition.onTaskCompleted())
	                                try {
	                                	System.out.println("closing after last part");
	                                    fileChannel.close();
	                                   
	        	            			File f = new File(localServerFilepath);
	        	                    	System.out.println("reading from " + localServerFilepath);
	                					InputStream inputStream = new FileInputStream(f);
	        	            			String sftpPath = config.getSftpFolder().concat("/").concat(filename);
	        	                    	System.out.println("writing to sftp "+sftpPath);
	        	            			sftp.sftpPutFromStream(inputStream, sftpPath);
	        	                    	System.out.println("finihed writing to sftp"+sftpPath);	                        	                        
	                                } catch (IOException e) {
	        	                    	setResponseKO(response, "error on completion", e);
	                                } catch (Exception e) {
	        	                    	setResponseKO(response, "error on completion", e);
									} finally {
										transferCondition.onTaskCompleted();
									}
	                        }

	                        @Override
	                        public void failed(Throwable exc, ByteBuffer attachment) {
	                            // there as an error while writing to disk, set an error flag
	                            errorFlag.set(true);
	                            System.out.printf("error saving file part {}\n", filePartOffset);
	                        }
	                    });
	                }
	            }).doOnComplete(() -> {
	                // all done, close the file channel
	            	System.out.println("done processing file parts");
	                if (closeCondition.canCloseOnComplete())
	                    try {
	                    	System.out.println("closing after complete");
	                        fileChannel.close();
	                        
	            			String fileName = filePart.filename();
	            			File f = new File(fileName);
	                    	System.out.println("reading from " + fileName);
        					InputStream inputStream = new FileInputStream(f);
	            			String sftpPath = config.getSftpFolder().concat("/").concat(fileName);
	                    	System.out.println("writing to sftp "+sftpPath);
	            			sftp.sftpPutFromStream(inputStream, sftpPath);
	                    	System.out.println("finihed writing to sftp"+sftpPath);	                        	                        
	                    } catch (IOException e) {
	                    	setResponseKO(response, "error on completion", e);
	        				Mono.error(e);
	                    } catch (Exception e) {
	                    	setResponseKO(response, "error on completion", e);
	        				Mono.error(e);
						} finally {
							if (!transferCondition.canCloseOnComplete()) {
								transferCondition.onTaskCompleted();
							}
						}

	            }).doOnError(t -> {
	                // ooops there was an error
	            	System.out.println("error processing file parts");
	                try {
	                    fileChannel.close();
	                } catch (IOException ignored) {
	                }
    				setResponseKO(response, "error transfering", t);
	            }).last().map(dataBuffer -> {
	            	boolean done = false;
	            	while(!done && !transferCondition.canCloseOnComplete()) {
	            		try {
							Thread.currentThread().sleep(1000);
						} catch (InterruptedException e) {
							done = true;
							setResponseKO(response, filePart.filename() + " error", e);
						}
	            	}
	            	if(errorFlag.get()) {
	            		setResponseKO(response, filePart.filename() + " error", null);
	            	} else {
	            		setResponseOK(response, filePart.filename() + " uploaded");
	            	}
	            	return response;
	            });
	        } catch (IOException e) {
				return Mono.just(setResponseKO(response, "error opening the file channel", e));
	        }
		} catch (Exception e) {
			return Mono.just(setResponseKO(response, "Upload Failed", e));
		}
	}
	private ResponseDTO setResponseOK(ResponseDTO response, String message) {
		if (response.getCode()==null)
			response.setCode("200");
		if (response.getMessage()==null)
			response.setMessage(message);
		return response;
	}
	private ResponseDTO setResponseKO(ResponseDTO response, String message, Throwable e) {
		if (response.getCode()==null)
			response.setCode("500");
		if (response.getMessage()==null)
			response.setMessage(message + e!=null?" : " + e.getMessage() : "");
		if(e!=null) e.printStackTrace();
		return response;
	}
}
