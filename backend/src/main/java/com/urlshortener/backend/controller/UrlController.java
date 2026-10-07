package com.urlshortener.backend.controller;
import com.urlshortener.backend.dto.ApiResponse;
import com.urlshortener.backend.dto.UrlDto;
import com.urlshortener.backend.security.UserDetailsImpl;
import com.urlshortener.backend.service.UrlService;
import com.urlshortener.backend.service.QrCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/urls")
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;
    private final QrCodeService qrCodeService;
    
    @PostMapping
    public ApiResponse<UrlDto.Response> createUrl(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody UrlDto.CreateRequest request) {
        return ApiResponse.success(urlService.createUrl(userDetails.getId(), request), "URL created");
    }
    
    @GetMapping
    public ApiResponse<Page<UrlDto.Response>> getUrls(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.success(urlService.getUserUrls(userDetails.getId(), PageRequest.of(page, size)), "URLs fetched");
    }
    
    @PatchMapping("/{id}")
    public ApiResponse<UrlDto.Response> updateUrl(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long id,
            @RequestBody UrlDto.UpdateRequest request) {
        return ApiResponse.success(urlService.updateUrl(userDetails.getId(), id, request), "URL updated");
    }
    
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteUrl(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id) {
        urlService.deleteUrl(userDetails.getId(), id);
        return ApiResponse.success(null, "URL deleted");
    }
    
    @GetMapping("/{alias}/qr")
    public ResponseEntity<byte[]> getQrCode(@PathVariable String alias) throws Exception {
        String fullUrl = "http://localhost:8080/" + alias; 
        byte[] qr = qrCodeService.generateQrCodeImage(fullUrl, 250, 250);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        return new ResponseEntity<>(qr, headers, HttpStatus.OK);
    }
}
