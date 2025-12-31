package mau.donate.controller;

import mau.donate.objects.User;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Profile;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/file")
public class FileController {

    // Profile picture
    @GetMapping("/avatar/{id}.png")
    @Cacheable(value = "profilePic", key = "#id")
    public ResponseEntity<byte[]> getProfilePic(@PathVariable Long id) {
        User user = User.getById(id);
        HttpHeaders headers = new HttpHeaders();
        if (user.Image == null) {
            headers.setLocation(URI.create("/img/default-pfp.png"));
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        } else {
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setCacheControl(CacheControl.maxAge(1, TimeUnit.HOURS).cachePublic());
            return new ResponseEntity<>(user.Image, headers, HttpStatus.FOUND);
        }
    }
}
