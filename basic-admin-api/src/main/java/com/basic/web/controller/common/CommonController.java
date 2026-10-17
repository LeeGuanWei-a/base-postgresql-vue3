package com.basic.web.controller.common;

import com.basic.common.config.BasicConfig;
import com.basic.common.constant.Constants;
import com.basic.common.core.domain.AjaxResult;
import com.basic.common.utils.SecurityUtils;
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
import org.springframework.security.access.prepost.PreAuthorize;
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
     * @description 按文件名从下载目录读取文件写回响应流；校验文件名合法性，并用规范化路径兜底防目录穿越，
     * 仅管理员可带 delete=true 在下载后删除该文件
     * @params [fileName, delete, response, request] fileName 下载目录下的文件名；delete 下载后是否删除；response 响应流；request 当前请求
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
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

            // 兜底防目录穿越：解析后的真实路径必须仍在下载目录内
            String downloadRoot = new java.io.File(BasicConfig.getDownloadPath()).getCanonicalPath();
            String canonical = new java.io.File(filePath).getCanonicalPath();
            if (!canonical.startsWith(downloadRoot))
            {
                throw new Exception(StringUtils.format("文件路径({})越界，不允许下载。 ", fileName));
            }

            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            FileUtils.setAttachmentResponseHeader(response, realFileName);
            FileUtils.writeBytes(filePath, response.getOutputStream());
            // 只有管理员才能带 delete=true 删文件：原来是任何登录用户都能用下载接口删服务器上的文件
            if (Boolean.TRUE.equals(delete))
            {
                if (SecurityUtils.isAdmin(SecurityUtils.getUserId()))
                {
                    FileUtils.deleteFile(filePath);
                }
                else
                {
                    log.warn("非管理员用户 {} 请求删除下载文件 {}，已忽略", SecurityUtils.getUsername(), fileName);
                }
            }
        }
        catch (Exception e)
        {
            log.error("下载文件失败", e);
        }
    }

    /**
     * @return AjaxResult 含文件访问 url、原始文件名与落盘文件名
     * @description 单文件上传：按 dirPath 参数决定子目录（只允许字母数字/下划线/中划线/斜杠，禁止以 / 开头与 ..），
     * 落盘后写入 sys_files 记录并返回访问地址
     * @params [file, request] file 上传的文件；request 当前请求，用于取 dirPath 参数
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @PreAuthorize("@ss.hasPermi('common:file:upload')")
    @PostMapping("/upload")
    public AjaxResult uploadFile(MultipartFile file, HttpServletRequest request) throws Exception
    {
        try
        {
            // 上传文件路径
            String filePath = null;
            String dirPath = request.getParameter("dirPath");
            if(StringUtils.isNotEmpty(dirPath)){
                // 只允许「字母数字/下划线/中划线/斜杠」组成的子目录，且不能以 / 开头、不能出现 ..
                if(StringUtils.contains(dirPath, "..") || dirPath.startsWith("/") || dirPath.startsWith("\\")
                        || !dirPath.matches("[A-Za-z0-9_\\-/]+")){
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
            log.error("上传文件失败", e);
            return AjaxResult.error(e.getMessage());
        }
    }


    /**
     * @return AjaxResult 含 urls、fileNames、newFileNames、originalFilenames 四个拼接串
     * @description 多文件上传：逐个落盘到上传目录并返回四组以分隔符拼接的字符串，供前端批量回显
     * @params [files] 待上传的文件列表
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @PreAuthorize("@ss.hasPermi('common:file:upload')")
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
     * @description 按资源路径读取本地文件写回响应流；校验资源名合法性并做路径越界兜底
     * @params [resource, request, response] resource 资源路径；request 当前请求；response 响应流
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
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
            // 兜底防目录穿越：解析后的真实路径必须仍在 profile 目录内
            String profileRoot = new java.io.File(localPath).getCanonicalPath();
            if (!new java.io.File(downloadPath).getCanonicalPath().startsWith(profileRoot))
            {
                throw new Exception(StringUtils.format("资源路径({})越界，不允许下载。 ", resource));
            }
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
