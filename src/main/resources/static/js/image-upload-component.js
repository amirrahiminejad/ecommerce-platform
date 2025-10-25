/**
 * Image Upload and Management Component
 * A comprehensive frontend component for handling image uploads with preview, drag & drop, and management features
 */

class ImageUploadManager {
    constructor(containerId, options = {}) {
        this.container = document.getElementById(containerId);
        this.options = {
            maxFiles: options.maxFiles || 10,
            maxFileSize: options.maxFileSize || 5 * 1024 * 1024, // 5MB
            acceptedTypes: options.acceptedTypes || ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/webp'],
            uploadUrl: options.uploadUrl || '/api/images/upload',
            deleteUrl: options.deleteUrl || '/api/images/delete',
            showPreview: options.showPreview !== false,
            allowReorder: options.allowReorder !== false,
            allowDelete: options.allowDelete !== false,
            theme: options.theme || 'default' // default, modern, minimal
        };
        
        this.images = [];
        this.draggedIndex = null;
        
        this.init();
    }
    
    init() {
        this.createHTML();
        this.bindEvents();
        this.applyTheme();
    }
    
    createHTML() {
        this.container.innerHTML = `
            <div class="image-upload-manager" data-theme="${this.options.theme}">
                <!-- Upload Area -->
                <div class="upload-area" id="uploadArea">
                    <div class="upload-content">
                        <i class="upload-icon bi bi-cloud-upload"></i>
                        <h4>Upload Images</h4>
                        <p>Drag & drop images here or click to browse</p>
                        <button type="button" class="btn btn-primary upload-btn">
                            <i class="bi bi-plus-circle me-2"></i>Choose Images
                        </button>
                        <div class="upload-info">
                            <small>Max ${this.options.maxFiles} files, ${this.formatFileSize(this.options.maxFileSize)} each</small>
                        </div>
                    </div>
                    <input type="file" id="fileInput" multiple accept="${this.options.acceptedTypes.join(',')}" style="display: none;">
                </div>
                
                <!-- Progress Bar -->
                <div class="upload-progress" id="uploadProgress" style="display: none;">
                    <div class="progress">
                        <div class="progress-bar" role="progressbar" style="width: 0%"></div>
                    </div>
                    <small class="progress-text">Uploading...</small>
                </div>
                
                <!-- Image Gallery -->
                <div class="image-gallery" id="imageGallery">
                    <!-- Images will be dynamically added here -->
                </div>
                
                <!-- Error Messages -->
                <div class="error-messages" id="errorMessages"></div>
            </div>
        `;
    }
    
    bindEvents() {
        const uploadArea = this.container.querySelector('#uploadArea');
        const fileInput = this.container.querySelector('#fileInput');
        const uploadBtn = this.container.querySelector('.upload-btn');
        
        // File input change
        fileInput.addEventListener('change', (e) => this.handleFileSelect(e));
        
        // Upload button click
        uploadBtn.addEventListener('click', () => fileInput.click());
        
        // Drag and drop events
        uploadArea.addEventListener('dragover', (e) => this.handleDragOver(e));
        uploadArea.addEventListener('dragleave', (e) => this.handleDragLeave(e));
        uploadArea.addEventListener('drop', (e) => this.handleDrop(e));
    }
    
    applyTheme() {
        const styles = document.createElement('style');
        styles.textContent = `
            .image-upload-manager {
                font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
            }
            
            .image-upload-manager[data-theme="default"] {
                --primary-color: #667eea;
                --secondary-color: #764ba2;
                --success-color: #28a745;
                --danger-color: #dc3545;
                --warning-color: #ffc107;
                --border-color: #dee2e6;
                --bg-light: #f8f9fa;
                --text-muted: #6c757d;
            }
            
            .image-upload-manager[data-theme="modern"] {
                --primary-color: #6366f1;
                --secondary-color: #8b5cf6;
                --success-color: #10b981;
                --danger-color: #ef4444;
                --warning-color: #f59e0b;
                --border-color: #e5e7eb;
                --bg-light: #f9fafb;
                --text-muted: #6b7280;
            }
            
            .image-upload-manager[data-theme="minimal"] {
                --primary-color: #000000;
                --secondary-color: #333333;
                --success-color: #22c55e;
                --danger-color: #ef4444;
                --warning-color: #eab308;
                --border-color: #e5e5e5;
                --bg-light: #fafafa;
                --text-muted: #737373;
            }
            
            .upload-area {
                border: 2px dashed var(--border-color);
                border-radius: 12px;
                padding: 40px 20px;
                text-align: center;
                background: var(--bg-light);
                transition: all 0.3s ease;
                cursor: pointer;
                margin-bottom: 20px;
            }
            
            .upload-area:hover, .upload-area.drag-over {
                border-color: var(--primary-color);
                background: rgba(102, 126, 234, 0.05);
                transform: translateY(-2px);
            }
            
            .upload-icon {
                font-size: 3rem;
                color: var(--primary-color);
                margin-bottom: 15px;
            }
            
            .upload-area h4 {
                color: #333;
                margin-bottom: 10px;
                font-weight: 600;
            }
            
            .upload-area p {
                color: var(--text-muted);
                margin-bottom: 20px;
            }
            
            .upload-btn {
                background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
                border: none;
                border-radius: 8px;
                padding: 10px 20px;
                font-weight: 500;
                transition: all 0.3s ease;
            }
            
            .upload-btn:hover {
                transform: translateY(-1px);
                box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
            }
            
            .upload-info {
                margin-top: 15px;
            }
            
            .upload-info small {
                color: var(--text-muted);
                font-size: 0.85rem;
            }
            
            .upload-progress {
                margin-bottom: 20px;
                padding: 15px;
                background: white;
                border-radius: 8px;
                border: 1px solid var(--border-color);
            }
            
            .progress {
                height: 8px;
                border-radius: 4px;
                background: var(--bg-light);
                overflow: hidden;
            }
            
            .progress-bar {
                background: linear-gradient(90deg, var(--primary-color), var(--secondary-color));
                border-radius: 4px;
                transition: width 0.3s ease;
            }
            
            .progress-text {
                display: block;
                text-align: center;
                margin-top: 8px;
                color: var(--text-muted);
            }
            
            .image-gallery {
                display: grid;
                grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
                gap: 15px;
                margin-top: 20px;
            }
            
            .image-item {
                position: relative;
                background: white;
                border-radius: 12px;
                overflow: hidden;
                box-shadow: 0 2px 8px rgba(0,0,0,0.1);
                transition: all 0.3s ease;
                cursor: grab;
            }
            
            .image-item:hover {
                transform: translateY(-2px);
                box-shadow: 0 4px 16px rgba(0,0,0,0.15);
            }
            
            .image-item.dragging {
                opacity: 0.5;
                transform: rotate(5deg);
                cursor: grabbing;
            }
            
            .image-preview {
                width: 100%;
                height: 150px;
                object-fit: cover;
                display: block;
            }
            
            .image-info {
                padding: 12px;
            }
            
            .image-name {
                font-size: 0.9rem;
                font-weight: 500;
                color: #333;
                margin-bottom: 4px;
                white-space: nowrap;
                overflow: hidden;
                text-overflow: ellipsis;
            }
            
            .image-size {
                font-size: 0.8rem;
                color: var(--text-muted);
            }
            
            .image-actions {
                position: absolute;
                top: 8px;
                right: 8px;
                display: flex;
                gap: 4px;
                opacity: 0;
                transition: opacity 0.3s ease;
            }
            
            .image-item:hover .image-actions {
                opacity: 1;
            }
            
            .action-btn {
                width: 32px;
                height: 32px;
                border-radius: 50%;
                border: none;
                display: flex;
                align-items: center;
                justify-content: center;
                font-size: 0.9rem;
                cursor: pointer;
                transition: all 0.3s ease;
            }
            
            .action-btn.delete {
                background: var(--danger-color);
                color: white;
            }
            
            .action-btn.delete:hover {
                background: #c82333;
                transform: scale(1.1);
            }
            
            .action-btn.preview {
                background: var(--primary-color);
                color: white;
            }
            
            .action-btn.preview:hover {
                background: var(--secondary-color);
                transform: scale(1.1);
            }
            
            .image-status {
                position: absolute;
                top: 8px;
                left: 8px;
                padding: 4px 8px;
                border-radius: 12px;
                font-size: 0.75rem;
                font-weight: 500;
                text-transform: uppercase;
            }
            
            .image-status.uploading {
                background: var(--warning-color);
                color: white;
            }
            
            .image-status.success {
                background: var(--success-color);
                color: white;
            }
            
            .image-status.error {
                background: var(--danger-color);
                color: white;
            }
            
            .error-messages {
                margin-top: 15px;
            }
            
            .error-message {
                background: #f8d7da;
                color: #721c24;
                padding: 12px;
                border-radius: 8px;
                margin-bottom: 10px;
                border: 1px solid #f5c6cb;
                display: flex;
                align-items: center;
                gap: 8px;
            }
            
            .error-message i {
                font-size: 1.1rem;
            }
            
            .empty-gallery {
                text-align: center;
                padding: 40px 20px;
                color: var(--text-muted);
            }
            
            .empty-gallery i {
                font-size: 3rem;
                margin-bottom: 15px;
                opacity: 0.5;
            }
            
            /* Animations */
            @keyframes fadeIn {
                from { opacity: 0; transform: translateY(20px); }
                to { opacity: 1; transform: translateY(0); }
            }
            
            .image-item {
                animation: fadeIn 0.3s ease;
            }
            
            /* Responsive */
            @media (max-width: 768px) {
                .image-gallery {
                    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
                    gap: 10px;
                }
                
                .upload-area {
                    padding: 30px 15px;
                }
                
                .upload-icon {
                    font-size: 2.5rem;
                }
            }
        `;
        
        if (!document.querySelector('#imageUploadStyles')) {
            styles.id = 'imageUploadStyles';
            document.head.appendChild(styles);
        }
    }
    
    handleFileSelect(event) {
        const files = Array.from(event.target.files);
        this.processFiles(files);
        event.target.value = ''; // Reset input
    }
    
    handleDragOver(event) {
        event.preventDefault();
        event.currentTarget.classList.add('drag-over');
    }
    
    handleDragLeave(event) {
        event.preventDefault();
        event.currentTarget.classList.remove('drag-over');
    }
    
    handleDrop(event) {
        event.preventDefault();
        event.currentTarget.classList.remove('drag-over');
        
        const files = Array.from(event.dataTransfer.files);
        this.processFiles(files);
    }
    
    processFiles(files) {
        this.clearErrors();
        
        // Filter valid files
        const validFiles = files.filter(file => this.validateFile(file));
        
        // Check max files limit
        if (this.images.length + validFiles.length > this.options.maxFiles) {
            this.showError(`Maximum ${this.options.maxFiles} files allowed`);
            return;
        }
        
        // Process each valid file
        validFiles.forEach(file => this.addImage(file));
    }
    
    validateFile(file) {
        // Check file type
        if (!this.options.acceptedTypes.includes(file.type)) {
            this.showError(`${file.name}: Invalid file type. Accepted types: ${this.options.acceptedTypes.join(', ')}`);
            return false;
        }
        
        // Check file size
        if (file.size > this.options.maxFileSize) {
            this.showError(`${file.name}: File too large. Maximum size: ${this.formatFileSize(this.options.maxFileSize)}`);
            return false;
        }
        
        return true;
    }
    
    addImage(file) {
        const imageId = this.generateId();
        const imageData = {
            id: imageId,
            file: file,
            name: file.name,
            size: file.size,
            status: 'uploading',
            preview: null,
            url: null
        };
        
        this.images.push(imageData);
        this.createImagePreview(file, imageData);
        this.renderGallery();
        
        // Upload file
        if (this.options.uploadUrl) {
            this.uploadImage(imageData);
        }
    }
    
    createImagePreview(file, imageData) {
        const reader = new FileReader();
        reader.onload = (e) => {
            imageData.preview = e.target.result;
            this.renderGallery();
        };
        reader.readAsDataURL(file);
    }
    
    async uploadImage(imageData) {
        const formData = new FormData();
        formData.append('image', imageData.file);
        formData.append('id', imageData.id);
        
        try {
            this.showProgress(true);
            
            const response = await fetch(this.options.uploadUrl, {
                method: 'POST',
                body: formData,
                headers: {
                    'X-Requested-With': 'XMLHttpRequest'
                }
            });
            
            if (response.ok) {
                const result = await response.json();
                imageData.status = 'success';
                imageData.url = result.url || result.path;
                imageData.serverId = result.id;
            } else {
                throw new Error(`Upload failed: ${response.statusText}`);
            }
        } catch (error) {
            console.error('Upload error:', error);
            imageData.status = 'error';
            this.showError(`Failed to upload ${imageData.name}: ${error.message}`);
        } finally {
            this.showProgress(false);
            this.renderGallery();
        }
    }
    
    async deleteImage(imageId) {
        const imageIndex = this.images.findIndex(img => img.id === imageId);
        if (imageIndex === -1) return;
        
        const imageData = this.images[imageIndex];
        
        // If image was uploaded to server, delete from server
        if (imageData.serverId && this.options.deleteUrl) {
            try {
                await fetch(`${this.options.deleteUrl}/${imageData.serverId}`, {
                    method: 'DELETE',
                    headers: {
                        'X-Requested-With': 'XMLHttpRequest'
                    }
                });
            } catch (error) {
                console.error('Delete error:', error);
                this.showError(`Failed to delete ${imageData.name}`);
                return;
            }
        }
        
        // Remove from local array
        this.images.splice(imageIndex, 1);
        this.renderGallery();
    }
    
    renderGallery() {
        const gallery = this.container.querySelector('#imageGallery');
        
        if (this.images.length === 0) {
            gallery.innerHTML = `
                <div class="empty-gallery">
                    <i class="bi bi-images"></i>
                    <p>No images uploaded yet</p>
                </div>
            `;
            return;
        }
        
        gallery.innerHTML = this.images.map((image, index) => `
            <div class="image-item" data-id="${image.id}" data-index="${index}" draggable="${this.options.allowReorder}">
                ${image.preview ? `<img src="${image.preview}" alt="${image.name}" class="image-preview">` : ''}
                
                <div class="image-status ${image.status}">
                    ${image.status}
                </div>
                
                <div class="image-actions">
                    ${this.options.showPreview ? `
                        <button class="action-btn preview" onclick="imageUploadManager.previewImage('${image.id}')" title="Preview">
                            <i class="bi bi-eye"></i>
                        </button>
                    ` : ''}
                    ${this.options.allowDelete ? `
                        <button class="action-btn delete" onclick="imageUploadManager.deleteImage('${image.id}')" title="Delete">
                            <i class="bi bi-trash"></i>
                        </button>
                    ` : ''}
                </div>
                
                <div class="image-info">
                    <div class="image-name" title="${image.name}">${image.name}</div>
                    <div class="image-size">${this.formatFileSize(image.size)}</div>
                </div>
            </div>
        `).join('');
        
        // Add drag and drop for reordering
        if (this.options.allowReorder) {
            this.addReorderFunctionality();
        }
    }
    
    addReorderFunctionality() {
        const imageItems = this.container.querySelectorAll('.image-item');
        
        imageItems.forEach(item => {
            item.addEventListener('dragstart', (e) => {
                this.draggedIndex = parseInt(e.target.dataset.index);
                e.target.classList.add('dragging');
            });
            
            item.addEventListener('dragend', (e) => {
                e.target.classList.remove('dragging');
                this.draggedIndex = null;
            });
            
            item.addEventListener('dragover', (e) => {
                e.preventDefault();
            });
            
            item.addEventListener('drop', (e) => {
                e.preventDefault();
                const dropIndex = parseInt(e.target.closest('.image-item').dataset.index);
                
                if (this.draggedIndex !== null && this.draggedIndex !== dropIndex) {
                    this.reorderImages(this.draggedIndex, dropIndex);
                }
            });
        });
    }
    
    reorderImages(fromIndex, toIndex) {
        const [removed] = this.images.splice(fromIndex, 1);
        this.images.splice(toIndex, 0, removed);
        this.renderGallery();
    }
    
    previewImage(imageId) {
        const image = this.images.find(img => img.id === imageId);
        if (!image || !image.preview) return;
        
        // Create modal for image preview
        const modal = document.createElement('div');
        modal.className = 'modal fade';
        modal.innerHTML = `
            <div class="modal-dialog modal-lg modal-dialog-centered">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">${image.name}</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body text-center">
                        <img src="${image.preview}" alt="${image.name}" class="img-fluid" style="max-height: 70vh;">
                    </div>
                    <div class="modal-footer">
                        <div class="me-auto">
                            <small class="text-muted">Size: ${this.formatFileSize(image.size)}</small>
                        </div>
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                    </div>
                </div>
            </div>
        `;
        
        document.body.appendChild(modal);
        const bsModal = new bootstrap.Modal(modal);
        bsModal.show();
        
        modal.addEventListener('hidden.bs.modal', () => {
            document.body.removeChild(modal);
        });
    }
    
    showProgress(show) {
        const progress = this.container.querySelector('#uploadProgress');
        progress.style.display = show ? 'block' : 'none';
        
        if (show) {
            const progressBar = progress.querySelector('.progress-bar');
            let width = 0;
            const interval = setInterval(() => {
                width += Math.random() * 30;
                if (width >= 90) {
                    clearInterval(interval);
                    width = 90;
                }
                progressBar.style.width = width + '%';
            }, 200);
        }
    }
    
    showError(message) {
        const errorContainer = this.container.querySelector('#errorMessages');
        const errorDiv = document.createElement('div');
        errorDiv.className = 'error-message';
        errorDiv.innerHTML = `
            <i class="bi bi-exclamation-triangle"></i>
            <span>${message}</span>
        `;
        
        errorContainer.appendChild(errorDiv);
        
        // Auto remove after 5 seconds
        setTimeout(() => {
            if (errorDiv.parentNode) {
                errorDiv.parentNode.removeChild(errorDiv);
            }
        }, 5000);
    }
    
    clearErrors() {
        const errorContainer = this.container.querySelector('#errorMessages');
        errorContainer.innerHTML = '';
    }
    
    formatFileSize(bytes) {
        if (bytes === 0) return '0 Bytes';
        const k = 1024;
        const sizes = ['Bytes', 'KB', 'MB', 'GB'];
        const i = Math.floor(Math.log(bytes) / Math.log(k));
        return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
    }
    
    generateId() {
        return 'img_' + Math.random().toString(36).substr(2, 9) + '_' + Date.now();
    }
    
    // Public API methods
    getImages() {
        return this.images;
    }
    
    getUploadedImages() {
        return this.images.filter(img => img.status === 'success');
    }
    
    clearAll() {
        this.images = [];
        this.renderGallery();
        this.clearErrors();
    }
    
    setImages(images) {
        this.images = images;
        this.renderGallery();
    }
}

// Make it globally available
window.ImageUploadManager = ImageUploadManager;

// Auto-initialize if data attribute is present
document.addEventListener('DOMContentLoaded', function() {
    const autoInitElements = document.querySelectorAll('[data-image-upload]');
    autoInitElements.forEach(element => {
        const options = JSON.parse(element.dataset.imageUpload || '{}');
        new ImageUploadManager(element.id, options);
    });
});
