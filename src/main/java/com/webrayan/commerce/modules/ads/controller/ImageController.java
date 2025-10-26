package com.webrayan.commerce.modules.ads.controller;

import com.webrayan.commerce.modules.ads.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/images")
@Tag(name = "Image Management", description = "APIs for image upload, processing, and management")
public class ImageController {

    @Autowired
    private ImageService imageService;

    @Operation(
        summary = "Upload image",
        description = "Upload an image file with automatic resizing to multiple dimensions. Supports JPEG, PNG, GIF, and WebP formats.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                schema = @Schema(implementation = ImageUploadRequest.class)
            )
        )
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Image uploaded successfully",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(
                    value = "{\n  \"message\": \"Image uploaded successfully and linked to ad.\",\n  \"filename\": \"product-image.jpg\",\n  \"sizes\": [\"150x150\", \"300x300\", \"600x600\", \"original\"]\n}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid file or file type not supported",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(
                    value = "{\n  \"error\": \"Invalid file type. Only JPEG, PNG, GIF, and WebP are supported.\"\n}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "413",
            description = "File size too large",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(
                    value = "{\n  \"error\": \"File size exceeds maximum limit of 10MB.\"\n}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error during upload",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(
                    value = "{\n  \"error\": \"Image upload failed: Storage unavailable\"\n}"
                )
            )
        )
    })
    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(
            @Parameter(
                description = "Image file to upload (max 10MB, JPEG/PNG/GIF/WebP)",
                required = true,
                content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
            )
            @RequestParam("file") MultipartFile file,
            
            @Parameter(
                description = "Optional advertisement ID to link the image to a specific ad",
                example = "123"
            )
            @RequestParam(value = "adId", required = false) Long adId) {
        try {
            String result = imageService.uploadImage(file, adId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Image upload failed: " + e.getMessage());
        }
    }

    @Operation(
        summary = "View image",
        description = "Retrieve an image in the specified size. Available sizes: original, 150x150, 300x300, 600x600"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Image retrieved successfully",
            content = @Content(
                mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE,
                schema = @Schema(type = "string", format = "binary")
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Image not found",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(
                    value = "{\n  \"error\": \"File not found\"\n}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid size parameter",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(
                    value = "{\n  \"error\": \"Invalid size. Available sizes: original, 150x150, 300x300, 600x600\"\n}"
                )
            )
        )
    })
    @GetMapping("/view")
    public byte[] viewImage(
            @Parameter(
                description = "Image size to retrieve",
                example = "300x300",
                schema = @Schema(allowableValues = {"original", "150x150", "300x300", "600x600"})
            )
            @RequestParam("size") String size,
            
            @Parameter(
                description = "Filename of the image",
                example = "product-image.jpg"
            )
            @RequestParam("filename") String filename) throws IOException {
        return imageService.viewImage(size, filename);
    }

    @Operation(
        summary = "Delete image",
        description = "Delete an image and all its resized versions. Optionally remove the association with an advertisement."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Image deleted successfully",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(
                    value = "{\n  \"message\": \"Image deleted successfully.\"\n}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Image not found",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(
                    value = "{\n  \"error\": \"Image not found.\"\n}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error during deletion",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(
                    value = "{\n  \"error\": \"Image delete failed: Permission denied\"\n}"
                )
            )
        )
    })
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteImage(
            @Parameter(
                description = "Filename of the image to delete",
                example = "product-image.jpg"
            )
            @RequestParam("filename") String filename,
            
            @Parameter(
                description = "Optional advertisement ID to remove image association",
                example = "123"
            )
            @RequestParam(value = "adId", required = false) Long adId) {
        try {
            boolean deleted = imageService.deleteImage(filename, adId);
            if (deleted) {
                return ResponseEntity.ok("Image deleted successfully.");
            } else {
                return ResponseEntity.status(404).body("Image not found.");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Image delete failed: " + e.getMessage());
        }
    }

    @Operation(
        summary = "List images by advertisement",
        description = "Retrieve all images associated with a specific advertisement"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Images retrieved successfully",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(
                    value = "[\n  {\n    \"id\": 1,\n    \"filename\": \"product-1.jpg\",\n    \"originalFilename\": \"product-1.jpg\",\n    \"size\": \"original\",\n    \"uploadDate\": \"2025-09-13T10:30:00\",\n    \"url\": \"/api/images/view?size=original&filename=product-1.jpg\"\n  }\n]"
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Advertisement not found",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(
                    value = "{\n  \"error\": \"Advertisement not found\"\n}"
                )
            )
        )
    })
    @GetMapping("/ad/{adId}")
    public ResponseEntity<?> listImagesByAd(
            @Parameter(
                description = "Advertisement ID to get images for",
                example = "123"
            )
            @PathVariable Long adId) {
        return ResponseEntity.ok(imageService.listImagesByAd(adId));
    }

    // Inner class for Swagger documentation
    public static class ImageUploadRequest {
        @Schema(description = "Image file to upload", type = "string", format = "binary")
        public MultipartFile file;
        
        @Schema(description = "Optional advertisement ID", example = "123")
        public Long adId;
    }
}
