# Image Upload & Management Component

A comprehensive, responsive, and feature-rich frontend component for handling image uploads with drag & drop, preview, and management capabilities.

## Features

- ✨ **Drag & Drop** - Intuitive drag and drop interface
- 🖼️ **Image Preview** - Real-time image previews with modal view
- 📱 **Responsive Design** - Works perfectly on mobile and desktop
- 🎨 **Multiple Themes** - Default, Modern, Minimal, Dark, and Colorful themes
- 🔄 **Reorderable** - Drag to reorder images
- 📊 **Progress Tracking** - Upload progress indication
- ⚡ **File Validation** - Size and type validation
- 🚫 **Error Handling** - Comprehensive error messages
- 🎯 **Customizable** - Extensive configuration options
- 🔌 **Easy Integration** - Simple JavaScript API

## Quick Start

### 1. Include Dependencies

```html
<!-- Bootstrap 5 (required) -->
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
<link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css" rel="stylesheet">

<!-- Inter Font (recommended) -->
<link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">

<!-- Component files -->
<script src="/js/image-upload-component.js"></script>
<link href="/css/image-upload-component.css" rel="stylesheet"> <!-- Optional: for additional themes -->
```

### 2. Create HTML Container

```html
<div id="myImageUploader"></div>
```

### 3. Initialize Component

```javascript
const imageUploader = new ImageUploadManager('myImageUploader', {
    maxFiles: 10,
    maxFileSize: 5 * 1024 * 1024, // 5MB
    theme: 'modern',
    uploadUrl: '/api/images/upload',
    deleteUrl: '/api/images/delete'
});
```

## Configuration Options

```javascript
const options = {
    // File constraints
    maxFiles: 10,                    // Maximum number of files
    maxFileSize: 5 * 1024 * 1024,   // Maximum file size in bytes (5MB)
    acceptedTypes: [                 // Accepted file types
        'image/jpeg', 'image/jpg', 
        'image/png', 'image/gif', 
        'image/webp'
    ],
    
    // API endpoints
    uploadUrl: '/api/images/upload', // Upload endpoint
    deleteUrl: '/api/images/delete', // Delete endpoint
    
    // UI options
    theme: 'default',               // Theme: 'default', 'modern', 'minimal', 'dark', 'colorful'
    showPreview: true,              // Show preview modal
    allowReorder: true,             // Allow drag-to-reorder
    allowDelete: true,              // Show delete buttons
};
```

## Themes

### Available Themes
- **default** - Classic blue gradient theme
- **modern** - Contemporary purple theme  
- **minimal** - Clean black and white theme
- **dark** - Dark mode theme
- **colorful** - Bright and vibrant theme

### Custom Layouts
Add CSS classes to container for different layouts:
- `compact` - Smaller layout
- `large` - Larger layout  
- `rounded` - Rounded corners
- `list-view` - List instead of grid
- `grid-2`, `grid-3`, `grid-4`, `grid-5` - Fixed grid columns
- `animated` - Enhanced animations

```html
<div id="uploader" class="compact rounded animated"></div>
```

## API Methods

```javascript
// Get all images (including uploading ones)
const allImages = imageUploader.getImages();

// Get only successfully uploaded images
const uploadedImages = imageUploader.getUploadedImages();

// Clear all images
imageUploader.clearAll();

// Set images programmatically
imageUploader.setImages([
    {
        id: 'img1',
        name: 'photo.jpg',
        size: 245760,
        status: 'success',
        preview: '/path/to/preview.jpg',
        url: '/path/to/image.jpg',
        serverId: 'server_id_123'
    }
]);

// Preview specific image
imageUploader.previewImage('imageId');
```

## Integration Examples

### Basic Form Integration

```html
<form action="/ads/create" method="post">
    <input type="text" name="title" placeholder="Title">
    
    <!-- Image uploader -->
    <div id="imageUploader"></div>
    <div id="imageInputs"></div> <!-- Hidden inputs will be added here -->
    
    <button type="submit">Submit</button>
</form>

<script>
const uploader = new ImageUploadManager('imageUploader');

// Add hidden inputs on form submit
document.querySelector('form').addEventListener('submit', function() {
    const images = uploader.getUploadedImages();
    const container = document.getElementById('imageInputs');
    
    container.innerHTML = '';
    images.forEach((img, index) => {
        const input = document.createElement('input');
        input.type = 'hidden';
        input.name = `imageUrls[${index}]`;
        input.value = img.url;
        container.appendChild(input);
    });
});
</script>
```

### Auto-Initialize with Data Attributes

```html
<div id="autoUploader" 
     data-image-upload='{
        "maxFiles": 5,
        "theme": "modern",
        "uploadUrl": "/api/upload"
     }'></div>
```

### AJAX Integration

```javascript
// Submit form data with images via AJAX
async function submitWithImages() {
    const images = uploader.getUploadedImages();
    const formData = {
        title: document.getElementById('title').value,
        description: document.getElementById('description').value,
        imageUrls: images.map(img => img.url),
        imageIds: images.map(img => img.serverId)
    };
    
    const response = await fetch('/api/ads/create', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(formData)
    });
    
    return response.json();
}
```

## Backend Integration

### Upload Endpoint Example (Spring Boot)

```java
@PostMapping("/api/images/upload")
public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile file,
                                   @RequestParam("id") String id) {
    try {
        // Save file
        String filename = fileService.saveImage(file);
        String url = "/uploads/images/" + filename;
        
        // Return response
        Map<String, Object> response = new HashMap<>();
        response.put("id", UUID.randomUUID().toString());
        response.put("url", url);
        response.put("filename", filename);
        
        return ResponseEntity.ok(response);
    } catch (Exception e) {
        return ResponseEntity.badRequest()
            .body(Map.of("error", e.getMessage()));
    }
}
```

### Delete Endpoint Example

```java
@DeleteMapping("/api/images/delete/{id}")
public ResponseEntity<?> deleteImage(@PathVariable String id) {
    try {
        fileService.deleteImage(id);
        return ResponseEntity.ok(Map.of("success", true));
    } catch (Exception e) {
        return ResponseEntity.badRequest()
            .body(Map.of("error", e.getMessage()));
    }
}
```

## Events and Customization

### Custom Event Handling

```javascript
const uploader = new ImageUploadManager('uploader');

// Override upload method for custom handling
const originalUpload = uploader.uploadImage;
uploader.uploadImage = async function(imageData) {
    console.log('Starting upload:', imageData.name);
    
    // Show custom loading
    showCustomLoader(true);
    
    try {
        await originalUpload.call(this, imageData);
        console.log('Upload successful:', imageData.name);
    } catch (error) {
        console.error('Upload failed:', error);
        showCustomError(error.message);
    } finally {
        showCustomLoader(false);
    }
};
```

### Custom Validation

```javascript
const uploader = new ImageUploadManager('uploader');

// Override validation
uploader.validateFile = function(file) {
    // Custom validation logic
    if (file.name.includes('forbidden')) {
        this.showError('Filename contains forbidden words');
        return false;
    }
    
    // Call original validation
    return ImageUploadManager.prototype.validateFile.call(this, file);
};
```

## Styling Customization

### CSS Custom Properties

```css
.image-upload-manager {
    --primary-color: #your-color;
    --secondary-color: #your-color;
    --success-color: #your-color;
    --danger-color: #your-color;
    --border-color: #your-color;
    --bg-light: #your-color;
}
```

### Custom Theme

```css
.image-upload-manager[data-theme="custom"] {
    --primary-color: #ff6b6b;
    --secondary-color: #4ecdc4;
    /* ... other properties */
}
```

## Browser Support

- Chrome 60+
- Firefox 55+
- Safari 12+
- Edge 79+
- Mobile browsers (iOS Safari, Chrome Mobile)

## Dependencies

- Bootstrap 5.3+ (CSS framework)
- Bootstrap Icons (icon font)
- Modern browser with ES6+ support

## File Structure

```
/static/
├── js/
│   └── image-upload-component.js     # Main component
├── css/
│   └── image-upload-component.css    # Optional additional styles
├── image-upload-demo.html            # Full demo page
└── image-upload-integration-examples.html  # Integration examples
```

## License

This component is free to use in your projects.

## Support

For issues and questions, please check the demo page and integration examples first. The component is designed to work without any backend changes - it's purely frontend focused.
