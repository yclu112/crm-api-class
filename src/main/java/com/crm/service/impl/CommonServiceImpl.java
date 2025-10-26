package com.crm.service.impl;

import com.aliyun.oss.OSSClient;
import com.crm.service.CommonService;
import com.crm.vo.FileUrlVO;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class CommonServiceImpl implements CommonService {

    @Resource
    private OSSClient ossClient;

    @Value("${aliyun.oss.bucketName}")
    private String bucketName;
    @Override
    public FileUrlVO upload(MultipartFile multipartFile) {
        String fileurl = " ";
        String originalFilename = multipartFile.getOriginalFilename();
        String fileType = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFileName=UUID.randomUUID()+fileType;
        InputStream inputStream = null;
        try {
            inputStream=multipartFile.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ossClient.putObject(bucketName, newFileName, inputStream);
        String fileUrl = "https://" + bucketName + "." + ossClient.getEndpoint().getHost() + "/" + newFileName;
        return new FileUrlVO(fileUrl);
    }
}
