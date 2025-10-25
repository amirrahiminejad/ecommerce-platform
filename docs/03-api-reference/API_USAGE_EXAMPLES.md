# 📡 API Usage Examples - Persia Bazaar

## فهرست نمونه‌های عملی

1. [Authentication Examples](#authentication-examples)
2. [User Management](#user-management)
3. [Ad Management](#ad-management)
4. [Error Handling Examples](#error-handling-examples)
5. [Validation Examples](#validation-examples)
6. [Frontend Integration](#frontend-integration)

## Authentication Examples

### 🔐 **1. Login (دریافت Token)**

**Request:**
```bash
curl -X POST http://localhost:8005/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "123"
  }'
```

**Success Response:**
```json
{
  "success": true,
  "message": "ورود موفق",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTYzNTc2MjQwMCwiZXhwIjoxNjM1Nzk4NDAwfQ.xyz",
    "type": "Bearer",
    "username": "admin",
    "roles": ["SYSTEM_ADMIN"]
  }
}
```

**Invalid Credentials:**
```json
{
  "success": false,
  "message": "نام کاربری یا رمز عبور اشتباه است",
  "errorCode": "AUTHENTICATION_FAILED"
}
```

### 📝 **2. Signup (ثبت‌نام)**

**Request:**
```bash
curl -X POST http://localhost:8005/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "email": "user@example.com",
    "password": "mypassword123"
  }'
```

**Success Response:**
```json
{
  "success": true,
  "message": "کاربر با موفقیت ایجاد شد",
  "data": {
    "id": 15,
    "username": "newuser",
    "email": "user@example.com",
    "status": "ACTIVE"
  }
}
```

## User Management

### 👥 **1. دریافت لیست کاربران (Admin Only)**

**Request:**
```bash
curl -X GET "http://localhost:8005/api/acl/users?page=0&size=10" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Response:**
```json
{
  "success": true,
  "message": "لیست کاربران",
  "data": [
    {
      "id": 1,
      "username": "admin",
      "email": "admin@example.com",
      "status": "ACTIVE",
      "roles": ["SYSTEM_ADMIN"],
      "createdAt": "2025-01-15T10:30:00"
    },
    {
      "id": 2,
      "username": "user1",
      "email": "user1@example.com",
      "status": "ACTIVE", 
      "roles": ["CUSTOMER"],
      "createdAt": "2025-02-01T14:20:00"
    }
  ]
}
```

### 👤 **2. مشاهده پروفایل شخصی**

**Request:**
```bash
curl -X GET http://localhost:8005/api/acl/users/profile \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Response:**
```json
{
  "success": true,
  "message": "اطلاعات پروفایل",
  "data": {
    "id": 5,
    "username": "john_doe",
    "email": "john@example.com",
    "status": "ACTIVE",
    "roles": ["CUSTOMER"],
    "profile": {
      "firstName": "John",
      "lastName": "Doe",
      "phone": "+98912345678"
    },
    "statistics": {
      "totalAds": 12,
      "activeAds": 8,
      "soldAds": 4
    }
  }
}
```

### ✏️ **3. ویرایش کاربر**

**Request:**
```bash
curl -X PUT http://localhost:8005/api/acl/users/5 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "newemail@example.com",
    "status": "INACTIVE"
  }'
```

**Success Response:**
```json
{
  "success": true,
  "message": "کاربر با موفقیت به‌روزرسانی شد",
  "data": {
    "id": 5,
    "username": "john_doe",
    "email": "newemail@example.com",
    "status": "INACTIVE",
    "updatedAt": "2025-09-09T16:45:00"
  }
}
```

### 🗑️ **4. حذف کاربر**

**Request:**
```bash
curl -X DELETE http://localhost:8005/api/acl/users/5 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Success Response:**
```json
{
  "success": true,
  "message": "کاربر با موفقیت حذف شد"
}
```

## Ad Management

### 📝 **1. ایجاد آگهی**

**Request:**
```bash
curl -X POST http://localhost:8005/api/ads \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "categoryId": 1,
    "title": "فروش خودرو پراید مدل 1400",
    "description": "خودرو در حالت عالی، کم کارکرد، رنگ سفید، بیمه تا پایان سال",
    "price": 45000000,
    "location": "تهران، منطقه 3",
    "tags": ["خودرو", "پراید", "1400"]
  }'
```

**Success Response:**
```json
{
  "success": true,
  "message": "آگهی با موفقیت ایجاد شد",
  "data": {
    "id": 123,
    "title": "فروش خودرو پراید مدل 1400",
    "price": 45000000,
    "status": "PENDING",
    "category": {
      "id": 1,
      "name": "خودرو"
    },
    "owner": {
      "username": "john_doe",
      "email": "john@example.com"
    },
    "createdAt": "2025-09-09T16:50:00",
    "expiresAt": "2025-10-09T16:50:00"
  }
}
```

### 🔍 **2. جستجوی آگهی‌ها**

**Request:**
```bash
curl -X GET "http://localhost:8005/api/ads?category=1&minPrice=30000000&maxPrice=50000000&page=0&size=20" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Response:**
```json
{
  "success": true,
  "message": "نتایج جستجو",
  "data": {
    "content": [
      {
        "id": 123,
        "title": "فروش خودرو پراید مدل 1400",
        "price": 45000000,
        "status": "ACTIVE",
        "category": "خودرو",
        "location": "تهران، منطقه 3",
        "publishedAt": "2025-09-09T17:00:00",
        "views": 45,
        "favorites": 3
      }
    ],
    "totalElements": 1,
    "totalPages": 1,
    "pageSize": 20,
    "currentPage": 0
  }
}
```

### ⭐ **3. افزودن به علاقه‌مندی‌ها**

**Request:**
```bash
curl -X POST http://localhost:8005/api/ads/123/favorite \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Success Response:**
```json
{
  "success": true,
  "message": "آگهی به علاقه‌مندی‌ها اضافه شد",
  "data": {
    "adId": 123,
    "userId": 5,
    "addedAt": "2025-09-09T17:15:00"
  }
}
```

## Error Handling Examples

### ❌ **1. خطای Validation**

**Invalid Request:**
```bash
curl -X POST http://localhost:8005/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "ab",
    "email": "invalid-email",
    "password": "123"
  }'
```

**Error Response:**
```json
{
  "success": false,
  "message": "خطا در اعتبارسنجی فیلدها",
  "data": {
    "username": "نام کاربری باید بین 3 تا 50 کاراکتر باشد",
    "email": "فرمت آدرس ایمیل صحیح نیست",
    "password": "رمز عبور باید حداقل 6 کاراکتر باشد"
  },
  "errorCode": "VALIDATION_ERROR",
  "timestamp": "2025-09-09T17:20:00"
}
```

### 🚫 **2. خطای دسترسی**

**Unauthorized Request:**
```bash
curl -X GET http://localhost:8005/api/acl/users \
  -H "Authorization: Bearer INVALID_TOKEN"
```

**Error Response:**
```json
{
  "success": false,
  "message": "توکن نامعتبر یا منقضی شده است",
  "errorCode": "AUTHENTICATION_FAILED",
  "timestamp": "2025-09-09T17:25:00"
}
```

### 🔒 **3. خطای مجوز**

**Forbidden Request:**
```bash
curl -X DELETE http://localhost:8005/api/acl/users/1 \
  -H "Authorization: Bearer USER_TOKEN"  # Token عادی، نه Admin
```

**Error Response:**
```json
{
  "success": false,
  "message": "شما مجوز دسترسی به این عملیات را ندارید",
  "errorCode": "ACCESS_DENIED",
  "timestamp": "2025-09-09T17:30:00"
}
```

### 🔍 **4. خطای پیدا نشدن منبع**

**Request:**
```bash
curl -X GET http://localhost:8005/api/acl/users/999 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Error Response:**
```json
{
  "success": false,
  "message": "کاربر با id '999' پیدا نشد",
  "errorCode": "RESOURCE_NOT_FOUND",
  "timestamp": "2025-09-09T17:35:00"
}
```

### ⚡ **5. خطای تداخل**

**Duplicate Username Request:**
```bash
curl -X POST http://localhost:8005/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",  # نام کاربری تکراری
    "email": "new@example.com",
    "password": "password123"
  }'
```

**Error Response:**
```json
{
  "success": false,
  "message": "تداخل در فیلد 'username' با مقدار 'admin'",
  "errorCode": "CONFLICT_ERROR",
  "timestamp": "2025-09-09T17:40:00"
}
```

## Validation Examples

### ✅ **1. Custom Price Validation**

**Valid Price:**
```json
{
  "price": 1500000  // ✅ معتبر
}
```

**Invalid Price:**
```json
{
  "price": 500      // ❌ کمتر از حد مجاز (1000)
}
```

**Error Response:**
```json
{
  "success": false,
  "message": "خطا در اعتبارسنجی فیلدها",
  "data": {
    "price": "قیمت باید بین 1000 تا 999999999 تومان باشد"
  },
  "errorCode": "VALIDATION_ERROR"
}
```

### 📧 **2. Email Validation**

**Valid Email:**
```json
{
  "email": "user@example.com"  // ✅ معتبر
}
```

**Invalid Email:**
```json
{
  "email": "invalid-email"     // ❌ فرمت نامعتبر
}
```

**Error Response:**
```json
{
  "success": false,
  "message": "خطا در اعتبارسنجی فیلدها",
  "data": {
    "email": "فرمت آدرس ایمیل صحیح نیست"
  },
  "errorCode": "VALIDATION_ERROR"
}
```

## Frontend Integration

### 🌐 **1. JavaScript/React Example**

```javascript
// API Helper Class
class BazaarAPI {
  constructor(baseURL = 'http://localhost:8005/api') {
    this.baseURL = baseURL;
    this.token = localStorage.getItem('jwt_token');
  }

  // Set token after login
  setToken(token) {
    this.token = token;
    localStorage.setItem('jwt_token', token);
  }

  // Generic API call
  async apiCall(endpoint, method = 'GET', data = null) {
    const headers = {
      'Content-Type': 'application/json',
    };

    if (this.token) {
      headers['Authorization'] = `Bearer ${this.token}`;
    }

    const config = {
      method,
      headers,
    };

    if (data) {
      config.body = JSON.stringify(data);
    }

    try {
      const response = await fetch(`${this.baseURL}${endpoint}`, config);
      const result = await response.json();

      if (!response.ok) {
        throw new Error(result.message || 'خطای شبکه');
      }

      return result;
    } catch (error) {
      console.error('API Error:', error);
      throw error;
    }
  }

  // Authentication
  async login(username, password) {
    const result = await this.apiCall('/auth/login', 'POST', {
      username,
      password
    });

    if (result.success) {
      this.setToken(result.data.token);
    }

    return result;
  }

  // User management
  async getUsers(page = 0, size = 10) {
    return await this.apiCall(`/acl/users?page=${page}&size=${size}`);
  }

  async createUser(userData) {
    return await this.apiCall('/acl/users', 'POST', userData);
  }

  // Ad management
  async createAd(adData) {
    return await this.apiCall('/ads', 'POST', adData);
  }

  async getAds(filters = {}) {
    const params = new URLSearchParams(filters).toString();
    return await this.apiCall(`/ads?${params}`);
  }
}

// Usage Example
const api = new BazaarAPI();

// Login
try {
  const loginResult = await api.login('admin', '123');
  console.log('Login successful:', loginResult.data);
} catch (error) {
  console.error('Login failed:', error.message);
}

// Create user
try {
  const newUser = await api.createUser({
    username: 'newuser',
    email: 'user@example.com',
    password: 'password123'
  });
  console.log('User created:', newUser.data);
} catch (error) {
  console.error('User creation failed:', error.message);
}
```

### ⚛️ **2. React Component Example**

```jsx
import React, { useState, useEffect } from 'react';

function UserList() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchUsers();
  }, []);

  const fetchUsers = async () => {
    try {
      setLoading(true);
      const api = new BazaarAPI();
      const result = await api.getUsers();
      
      if (result.success) {
        setUsers(result.data);
      } else {
        setError(result.message);
      }
    } catch (err) {
      setError('خطا در دریافت اطلاعات');
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <div>در حال بارگذاری...</div>;
  if (error) return <div>خطا: {error}</div>;

  return (
    <div>
      <h2>لیست کاربران</h2>
      {users.map(user => (
        <div key={user.id} className="user-card">
          <h3>{user.username}</h3>
          <p>ایمیل: {user.email}</p>
          <p>وضعیت: {user.status}</p>
          <p>نقش‌ها: {user.roles.join(', ')}</p>
        </div>
      ))}
    </div>
  );
}
```

### 📱 **3. Mobile App (React Native) Example**

```javascript
// services/api.js
import AsyncStorage from '@react-native-async-storage/async-storage';

class MobileBazaarAPI {
  constructor() {
    this.baseURL = 'http://10.0.2.2:8005/api'; // Android emulator
    // this.baseURL = 'http://localhost:8005/api'; // iOS simulator
  }

  async getToken() {
    return await AsyncStorage.getItem('jwt_token');
  }

  async setToken(token) {
    await AsyncStorage.setItem('jwt_token', token);
  }

  async apiCall(endpoint, method = 'GET', data = null) {
    const token = await this.getToken();
    
    const headers = {
      'Content-Type': 'application/json',
    };

    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }

    const config = {
      method,
      headers,
    };

    if (data) {
      config.body = JSON.stringify(data);
    }

    const response = await fetch(`${this.baseURL}${endpoint}`, config);
    return await response.json();
  }

  async login(username, password) {
    const result = await this.apiCall('/auth/login', 'POST', {
      username,
      password
    });

    if (result.success) {
      await this.setToken(result.data.token);
    }

    return result;
  }
}

export default new MobileBazaarAPI();
```

### 🔄 **4. Error Handling in Frontend**

```javascript
// Error Handler Utility
class ErrorHandler {
  static handle(error, showToast = true) {
    let message = 'خطای نامشخص';
    
    if (error.response) {
      // API Error Response
      const { data } = error.response;
      message = data.message || 'خطای سرور';
      
      if (data.errorCode === 'VALIDATION_ERROR' && data.data) {
        // Validation errors
        const fieldErrors = Object.values(data.data).join(', ');
        message = `خطاهای اعتبارسنجی: ${fieldErrors}`;
      } else if (data.errorCode === 'AUTHENTICATION_FAILED') {
        // Redirect to login
        this.redirectToLogin();
        message = 'لطفا دوباره وارد شوید';
      }
    } else if (error.request) {
      // Network Error
      message = 'خطا در اتصال به سرور';
    }

    if (showToast) {
      this.showToast(message);
    }

    return message;
  }

  static redirectToLogin() {
    // Remove token
    localStorage.removeItem('jwt_token');
    // Redirect to login page
    window.location.href = '/login';
  }

  static showToast(message) {
    // Implementation depends on your toast library
    console.error(message);
  }
}

// Usage in React
const createUser = async (userData) => {
  try {
    const result = await api.createUser(userData);
    if (result.success) {
      toast.success('کاربر با موفقیت ایجاد شد');
      return result.data;
    }
  } catch (error) {
    ErrorHandler.handle(error);
  }
};
```

---

**🎯 این نمونه‌ها همه‌چیزی هستند که برای شروع کار با API نیاز دارید!** 

برای اطلاعات بیشتر به `docs/IMPLEMENTATION_GUIDE.md` مراجعه کنید.
