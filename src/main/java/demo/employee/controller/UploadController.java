package demo.employee.controller;

import demo.employee.service.upload.UploadService;
import lombok.RequiredArgsConstructor;
import org.hibernate.sql.ast.spi.SqlAliasBase;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping( "/uploads")
@RequiredArgsConstructor
public class UploadController {
    private final UploadService uploadService;

    @GetMapping
    public String form() {
        return "form";
    }

    @PostMapping
    public String handleUpload(@RequestParam(name = "file") MultipartFile file,Model model) {
        String url = uploadService.upload(file);
        model.addAttribute("url", url);
        return "form";
    }

}
