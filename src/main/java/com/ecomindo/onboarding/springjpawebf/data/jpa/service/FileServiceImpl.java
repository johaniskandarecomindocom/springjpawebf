package com.ecomindo.onboarding.springjpawebf.data.jpa.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;

import com.ecomindo.onboarding.springjpawebf.config.Config;
import com.ecomindo.onboarding.springjpawebf.data.jpa.dto.ResponseDTO;
import com.ecomindo.onboarding.springjpawebf.util.SftpUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import reactor.core.publisher.Mono;

@Service
public class FileServiceImpl implements FileService {

	@Autowired
	Config config;

	@Override
	public Mono<String> upload(FilePart filePart) throws Exception {
		ResponseDTO response = new ResponseDTO();
		try {
			SftpUtil sftp = new SftpUtil(config.getSftpHost(), config.getSftpPort(), config.getSftpUsername(),
					config.getSftpPassword(), config.getSftpFolder());

	        System.out.printf("handling file upload {}\n", filePart.filename());

	        response.setCode("200");
			response.setMessage("Upload Success");
	        // if a file with the same name already exists in a repository, delete and recreate it
	        final String filename = filePart.filename();
	        File file = new File(filename);
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
	                                } catch (IOException ignored) {
	                                    ignored.printStackTrace();
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
        					InputStream inputStream = new FileInputStream(f);
	            			String sftpPath = config.getSftpFolder().concat("/").concat(fileName);
	            			sftp.sftpPutFromStream(inputStream, sftpPath);
	                        
	                    } catch (IOException ignored) {
	                    } catch (Exception e) {
	        				e.printStackTrace();
	        				Mono.error(e);
						}

	            }).doOnError(t -> {
	                // ooops there was an error
	            	System.out.println("error processing file parts");
	                try {
	                    fileChannel.close();
	                } catch (IOException ignored) {
	                }
    				response.setCode("500");
    				response.setMessage("error transfering");
    				ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    				String json = null;
    				try {
    					json = ow.writeValueAsString(response);
					} catch (JsonProcessingException e1) {
						e1.printStackTrace();
					}
	                // take last, map to a status string
	            }).last().map(dataBuffer -> filePart.filename() + " " + (errorFlag.get() ? "error" : "uploaded"));
	        } catch (IOException e) {
				response.setCode("500");
				response.setMessage("error opening the file channel");
				e.printStackTrace();
				ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
				String json = ow.writeValueAsString(response);
				return Mono.just(json);
	        }
		} catch (Exception e) {
			response.setCode("500");
			response.setMessage("Upload Failed");
			e.printStackTrace();
			ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
			String json = ow.writeValueAsString(response);
			return Mono.just(json);
		}
	}
}
