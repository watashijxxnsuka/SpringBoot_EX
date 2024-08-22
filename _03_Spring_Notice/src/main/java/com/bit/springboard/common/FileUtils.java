package com.bit.springboard.common;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.bit.springboard.config.NaverConfiguration;
import com.bit.springboard.dto.BoardFileDto;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Component
public class FileUtils {
    private final AmazonS3 s3;

    public FileUtils(NaverConfiguration naverConfiguration) {
        s3 = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(
                                naverConfiguration.getEndPoint(),
                                naverConfiguration.getRegionName()
                        )
                )
                .withCredentials(
                        new AWSStaticCredentialsProvider(
                                new BasicAWSCredentials(
                                        naverConfiguration.getAccessKey(),
                                        naverConfiguration.getSecretKey()
                                )
                        )
                )
                .build();
    }

    public BoardFileDto parserFileInfo(MultipartFile multipartFile, String directory) {
        String bucketName = "bitcamp150";

        BoardFileDto boardFileDto = new BoardFileDto();

        // 다른 사용자가 같은 파일명의 파일을 업로드 했을 때
        // 덮어써지는 것을 방지하기 위해서 파일명을 랜덤값_날짜시간_파일명으로 지정
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Date nowDate = new Date();

        String nowDateStr = format.format(nowDate);

        UUID uuid = UUID.randomUUID();

        String fileName =  uuid.toString() + "_" + nowDateStr + "_" + multipartFile.getOriginalFilename();

        // Object Storage에 파일 업로드
        try(InputStream fileInputStream = multipartFile.getInputStream()) {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(multipartFile.getContentType());

            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName,
                    directory + fileName,
                    fileInputStream,
                    objectMetadata
            ).withCannedAcl(CannedAccessControlList.PublicRead);

            s3.putObject(putObjectRequest);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        File uploadFile = new File(directory + fileName);
        String type = "";

        try {
            type = Files.probeContentType(uploadFile.toPath());
        } catch(IOException ie) {
            System.out.println(ie.getMessage());
        }

        if(!type.equals("")) {
            if(type.startsWith("image")) {
                boardFileDto.setFiletype("image");
            } else {
                boardFileDto.setFiletype("etc");
            }
        } else {
            boardFileDto.setFiletype("etc");
        }

        boardFileDto.setFilename(fileName);
        boardFileDto.setFileoriginname(multipartFile.getOriginalFilename());
        boardFileDto.setFilepath(directory);

        return boardFileDto;
    }

    public void deleteFile(String directory, String fileName) {
        String bucketName = "bitcamp150";

        s3.deleteObject(new DeleteObjectRequest(bucketName, directory + fileName));
    }
}
