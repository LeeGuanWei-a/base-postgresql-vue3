package com.basic.web.controller.common;

import com.basic.common.config.BasicConfig;
import com.basic.common.constant.Constants;
import com.basic.common.core.domain.AjaxResult;
import com.basic.common.utils.StringUtils;
import com.basic.common.utils.file.FileUploadUtils;
import com.basic.common.utils.file.FileUtils;
import com.basic.framework.config.ServerConfig;
import com.basic.system.domain.SysFiles;
import com.basic.system.mapper.SysFilesMapper;
import com.basic.system.service.ISysFilesService;
import com.basic.system.service.impl.SysFilesServiceImpl;
import com.core.domain.Demo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通用请求处理
 * 
 * @author basic
 */
@RestController
@RequestMapping("/common")
public class CommonController
{
    private static final Logger log = LoggerFactory.getLogger(CommonController.class);

    @Autowired
    private ServerConfig serverConfig;
    @Autowired
    private ISysFilesService sysFilesService;

    private static final String FILE_DELIMETER = ",";

    /**
     * 通用下载请求
     * 
     * @param fileName 文件名称
     * @param delete 是否删除
     */
    @GetMapping("/download")
    public void fileDownload(String fileName, Boolean delete, HttpServletResponse response, HttpServletRequest request)
    {
        try
        {
            if (!FileUtils.checkAllowDownload(fileName))
            {
                throw new Exception(StringUtils.format("文件名称({})非法，不允许下载。 ", fileName));
            }
            String realFileName = System.currentTimeMillis() + fileName.substring(fileName.indexOf("_") + 1);
            String filePath = BasicConfig.getDownloadPath() + fileName;

            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            FileUtils.setAttachmentResponseHeader(response, realFileName);
            FileUtils.writeBytes(filePath, response.getOutputStream());
            if (delete)
            {
                FileUtils.deleteFile(filePath);
            }
        }
        catch (Exception e)
        {
            log.error("下载文件失败", e);
        }
    }

    /**
     * 通用上传请求（单个）
     */
    @PostMapping("/upload")
    public AjaxResult uploadFile(MultipartFile file, HttpServletRequest request) throws Exception
    {
        try
        {
            // 上传文件路径
            String filePath = null;
            String dirPath = request.getParameter("dirPath");
            if(StringUtils.isNotEmpty(dirPath)){
                if(StringUtils.contains(dirPath, "..")){
                    throw new Exception(StringUtils.format("文件夹名称({})非法，不允许上传。 ", dirPath));
                }
                filePath = BasicConfig.getProfile() + "/" + dirPath;
            }else{
                filePath = BasicConfig.getUploadPath();
            }

            // 上传并返回新文件名称
            String fileName = FileUploadUtils.upload(filePath, file);
            String url = serverConfig.getUrl() + fileName;
            AjaxResult ajax = AjaxResult.success();
            ajax.put("url", url);//完整的网络地址
            ajax.put("fileName", fileName);//文件的相对路静
            ajax.put("newFileName", FileUtils.getName(fileName));//文件的新名字
            ajax.put("originalFilename", file.getOriginalFilename());//文件的原始名字

            //保存到文件表
            SysFiles files = new SysFiles();
            files.setRelativePath(fileName.replace(Constants.RESOURCE_PREFIX,""));
            files.setAbsolutePath(BasicConfig.getProfile() + files.getRelativePath());
            files.setIdentity(request.getParameter("identity"));
            files.setName(file.getOriginalFilename());
            files.setSize(file.getSize());
            sysFilesService.saveOrUpdate(files);

            ajax.put("bean", new HashMap<String,Object>(){{
                put("id",files.getId());
                put("relativePath",files.getRelativePath());
                put("size",files.getSize());
            }});

            return ajax;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return AjaxResult.error(e.getMessage());
        }
    }


    /**
     * 通用上传请求（多个）
     */
    @PostMapping("/uploads")
    public AjaxResult uploadFiles(List<MultipartFile> files) throws Exception
    {
        try
        {
            // 上传文件路径
            String filePath = BasicConfig.getUploadPath();
            List<String> urls = new ArrayList<String>();
            List<String> fileNames = new ArrayList<String>();
            List<String> newFileNames = new ArrayList<String>();
            List<String> originalFilenames = new ArrayList<String>();
            for (MultipartFile file : files)
            {
                // 上传并返回新文件名称
                String fileName = FileUploadUtils.upload(filePath, file);
                String url = serverConfig.getUrl() + fileName;
                urls.add(url);
                fileNames.add(fileName);
                newFileNames.add(FileUtils.getName(fileName));
                originalFilenames.add(file.getOriginalFilename());
            }
            AjaxResult ajax = AjaxResult.success();
            ajax.put("urls", StringUtils.join(urls, FILE_DELIMETER));
            ajax.put("fileNames", StringUtils.join(fileNames, FILE_DELIMETER));
            ajax.put("newFileNames", StringUtils.join(newFileNames, FILE_DELIMETER));
            ajax.put("originalFilenames", StringUtils.join(originalFilenames, FILE_DELIMETER));
            return ajax;
        }
        catch (Exception e)
        {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 本地资源通用下载
     */
    @GetMapping("/download/resource")
    public void resourceDownload(String resource, HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        try
        {
            if (!FileUtils.checkAllowDownload(resource))
            {
                throw new Exception(StringUtils.format("资源文件({})非法，不允许下载。 ", resource));
            }
            // 本地资源路径
            String localPath = BasicConfig.getProfile();
            // 数据库资源地址
            String downloadPath = localPath + StringUtils.substringAfter(resource, Constants.RESOURCE_PREFIX);
            // 下载名称
            String downloadName = StringUtils.substringAfterLast(downloadPath, "/");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            FileUtils.setAttachmentResponseHeader(response, downloadName);
            FileUtils.writeBytes(downloadPath, response.getOutputStream());
        }
        catch (Exception e)
        {
            log.error("下载文件失败", e);
        }
    }
}
