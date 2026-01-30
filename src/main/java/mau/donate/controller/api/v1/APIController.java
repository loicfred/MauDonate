package mau.donate.controller.api.v1;

import mau.donate.objects.Association;
import mau.donate.objects.Campaign;
import mau.donate.objects.Donation_Request;
import mau.donate.objects.User;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class APIController {

    private final CacheManager cacheManager;
    public APIController(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @GetMapping("/img/avatar/{id}")
    @Cacheable(value = "IMG", key = "'PFP' + #id")
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

    @GetMapping("/img/association/{id}")
    @Cacheable(value = "IMG", key = "'ASO' + #id")
    public ResponseEntity<byte[]> getAssociationPic(@PathVariable Long id) {
        Association association = Association.getById(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setCacheControl(CacheControl.maxAge(1, TimeUnit.HOURS).cachePublic());
        return new ResponseEntity<>(association.Image, headers, HttpStatus.FOUND);
    }

    @GetMapping("/img/campaign/{id}")
    @Cacheable(value = "IMG", key = "'CAM' + #id")
    public ResponseEntity<byte[]> getCampaignPic(@PathVariable Long id) {
        Campaign campaign = Campaign.getById(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setCacheControl(CacheControl.maxAge(1, TimeUnit.HOURS).cachePublic());
        return new ResponseEntity<>(campaign.Image, headers, HttpStatus.FOUND);
    }

    @GetMapping("/searchbar")
    @Cacheable(value = "API", key = "'SEARCH'")
    public ResponseEntity<List<SearchItem>> searchBar() {
        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl(CacheControl.maxAge(1, TimeUnit.HOURS).cachePublic());

        List<SearchItem> reqs = Donation_Request.getAllWhere(Donation_Request.class, "Approved").stream().map(SearchItem::new).collect(Collectors.toList());
        reqs.addAll(Campaign.getAll(Campaign.class).stream().map(SearchItem::new).toList());
        return new ResponseEntity<>(reqs, headers, HttpStatus.OK);
    }

    @GetMapping("/clearcache")
    public ResponseEntity<String> clearcache() {
        cacheManager.getCacheNames().forEach(cache -> Objects.requireNonNull(cacheManager.getCache(cache)).clear());
        return new ResponseEntity<>("All caches cleared !", HttpStatus.OK);
    }

    public static class SearchItem {
        public long id;
        public String name;
        public String description;
        public String type;

        public SearchItem(Donation_Request req) {
            this.id = req.ID;
            this.name = req.Title;
            this.description = req.Message;
            this.type = "request";
        }
        public SearchItem(Campaign cam) {
            this.id = cam.ID;
            this.name = cam.Title;
            this.description = cam.Message;
            this.type = "campaign";
        }
    }
}
