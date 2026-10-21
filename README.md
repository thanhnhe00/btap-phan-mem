# COURSE MANAGEMENT - BÀI TẬP LẬP TRÌNH ỨNG DỤNG DI ĐỘNG

Ứng dụng Android **Kotlin + XML**, kết nối **MockAPI.io (REST API)** để quản lý khóa học. Đây là mã nguồn phục vụ bài tập theo PDF `Bai_tap_Mobile_Quan_ly_khoa_hoc_MockAPI.pdf`.

## 1. Công nghệ

| Thành phần | Công nghệ |
|---|---|
| Ứng dụng | Android Studio, Kotlin, XML Views, ViewBinding |
| Backend | MockAPI.io, `categories` và `courses` |
| REST + JSON | Retrofit 2.11.0, Gson Converter |
| Bất đồng bộ | Kotlin Coroutines, `lifecycleScope` |
| Danh sách | RecyclerView + ListAdapter/DiffUtil |
| Tải ảnh | Glide 4.16.0 |
| Video trailer | Media3 ExoPlayer 1.5.1, phát *trong ứng dụng* |
| Material UI | Material Components 1.12.0 |
| Android | compileSdk 35, targetSdk 34, minSdk 24 |
| Build | Android Gradle Plugin 8.7.3, Gradle 8.9, JDK 17 |

Cấu trúc kiến trúc tách **data/model -> data/remote -> data/repository -> ui**. Mã nguồn UI không gửi HTTP trực tiếp; Retrofit được gọi thông qua Repository. Đơn giản để phù hợp bài tập, không thêm database hoặc backend riêng.

## 2. Cấu trúc project

```text
CourseManagementAndroid/
├── app/
│   ├── build.gradle.kts
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── java/com/example/coursemanagement/
│       │   │   ├── data/
│       │   │   │   ├── model/                   # Course, Category, POST/PUT Payloads
│       │   │   │   ├── remote/                  # Retrofit & API endpoints
│       │   │   │   └── repository/              # Tầng tương tác API
│       │   │   ├── ui/
│       │   │   │   ├── courses/                 # Danh sách, Chi tiết, Thêm/Sửa
│       │   │   │   ├── categories/              # CRUD danh mục
│       │   │   │   └── video/                   # ExoPlayer
│       │   │   └── util/                        # Kiểm tra input, lỗi, format tiền
│       │   └── res/layout/                     # XML giao diện
│       └── test/                               # Unit test validator
├── gradle/wrapper/gradle-wrapper.properties
├── sample-data/categories.json                # 3 danh mục mẫu
├── sample-data/courses.json                   # 10 khóa học mẫu
├── scripts/seed_mockapi.py                    # POST dữ liệu mẫu lên MockAPI
├── gradle.properties                          # Sửa mockApiBaseUrl tại đây
└── README.md
```

## 3. Tạo MockAPI (BẮT BUỘC trước khi chạy app)

1. Mở https://mockapi.io/ và tạo project mới; lấy base URL của project. Ví dụ minh họa: `https://YOUR_PROJECT_ID.mockapi.io/api/v1/`. **Đây chưa phải URL thật**.
2. Tạo chính xác **2 resources** (đúng chữ thường):
   - `categories`: `name` (String), `description` (String); `id` là String do MockAPI tự sinh.
   - `courses`: `title` (String), `description` (String), `categoryId` (String), `instructor` (String), `price` (Number), `duration` (Number), `thumbnail` (String), `trailerUrl` (String), `createdAt` (String); `id` tự sinh.
3. Không cần tạo quan hệ khóa ngoại trên MockAPI. Ứng dụng liên kết `courses.categoryId = categories.id`.
4. Đảm bảo mở được 2 đường dẫn trên trình duyệt (GET JSON):
   - `https://<PROJECT_ID>.mockapi.io/api/v1/categories`
   - `https://<PROJECT_ID>.mockapi.io/api/v1/courses`

**Chú ý**: MockAPI có thể sinh `createdAt` tự động theo mặc định. Nếu cấu hình trường này theo mẫu bài tập thì để kiểu String, và giữ đúng key `createdAt`.

## 4. Đưa dữ liệu mẫu lên MockAPI

Máy tính cần Python 3.9+ (không cần cài pip). Chạy terminal tại thư mục gốc project:

```bash
python scripts/seed_mockapi.py https://<PROJECT_ID>.mockapi.io/api/v1/
```

Trên Windows nếu lệnh `python` không hoạt động, thử `py scripts/seed_mockapi.py https://<PROJECT_ID>.mockapi.io/api/v1/`.

Script sẽ:
- GET `/categories`, GET `/courses` để kiểm tra dữ liệu hiện có.
- POST danh mục còn thiếu (theo `name`), tự lấy **ID thực tế** MockAPI sinh ra.
- POST khóa học còn thiếu (theo `title`), ánh xạ `categoryId` sang ID danh mục thực tế.
- Không ghi đè hoặc xóa bản ghi cũ; có thể chạy lại mà không tạo bản ghi trùng `name`/`title` trong điều kiện bình thường.

> Ghi chú: `thumbnail` dùng ảnh từ `picsum.photos`, trailer dùng các tệp MP4 mẫu công khai của Google. Trailer là video DEMO kiểm tra chức năng phát, không phải trailer học thuật của từng khóa học. Các URL cần kết nối Internet và có thể thay nếu dịch vụ công khai ngừng hoạt động.

## 5. Cấu hình URL trong Android Studio

1. Giải nén ZIP, chọn **Android Studio > Open** rồi mở thư mục `CourseManagementAndroid` (thư mục chứa `settings.gradle.kts`).
2. Chờ Android Studio tải Android SDK 35, Gradle và dependencies lần đầu (cần Internet).
3. Mở **`gradle.properties` ở gốc project** và sửa:

```properties
mockApiBaseUrl=https://<PROJECT_ID>.mockapi.io/api/v1/
```

4. URL phải là **HTTPS** và **kết thúc bằng dấu `/`**. Không thêm các endpoint `categories`/`courses` vào base URL.
5. Chọn **File > Sync Project with Gradle Files**.
6. Tạo emulator Android API 24+ hoặc cắm điện thoại bật USB debugging; chọn module **app** và nhấn **Run ▶**.

**Gradle Wrapper lưu ý**: Môi trường đóng gói không có sẵn `gradle-wrapper.jar`, nên ZIP có file `gradle-wrapper.properties` nhưng không kèm script `gradlew`/`gradlew.bat` và wrapper jar. Android Studio có thể dùng URL phân phối Gradle 8.9 trong file properties khi import project; nếu IDE yêu cầu wrapper đầy đủ, cài Gradle 8.9 hoặc dùng Gradle cài sẵn, chạy `gradle wrapper --gradle-version 8.9` tại project rồi Sync. Việc build/sync thực tế cần Android SDK và Internet trên máy của bạn.

## 6. Các chức năng và vị trí mã nguồn

| Chức năng | Màn hình | Endpoint |
|---|---|---|
| Danh sách khóa học, tìm kiếm tên, lọc danh mục | `CourseListActivity.kt` | GET `/courses`, GET `/categories` |
| Chi tiết khóa học, tên danh mục | `CourseDetailActivity.kt` | GET `/courses/{id}`, GET `/categories` |
| Thêm khóa học, preview ảnh | `CourseFormActivity.kt` | POST `/courses` |
| Sửa khóa học | `CourseFormActivity.kt` | PUT `/courses/{id}` |
| Xóa khóa học có xác nhận | `CourseDetailActivity.kt` | DELETE `/courses/{id}` |
| Danh sách, thêm, sửa, xóa danh mục | `CategoryManagementActivity.kt` | GET/POST/PUT/DELETE `/categories` |
| Chặn xóa danh mục đang có khóa học | `CourseRepository.kt` | GET `/courses` trước DELETE |
| Phát video trailer trong ứng dụng | `TrailerActivity.kt` | MP4 URL bằng Media3 ExoPlayer |
| Validate tên, mô tả, giảng viên, học phí, thời lượng, categoryId, ảnh/video URL | `CourseValidator.kt` | Client-side validation |

## 7. Kiểm tra bài tập theo trình tự

1. **List**: Vào app, thấy >=10 khóa học có ảnh, tên, giảng viên, danh mục và giá; chạm vào một mục để xem chi tiết.
2. **Search/filter**: Nhập `Android`; sau đó xóa và chọn danh mục `Trí tuệ nhân tạo` để xem kết quả theo danh mục.
3. **Detail**: Kiểm tra toàn bộ trường thông tin, tên danh mục được lấy từ API; chạm **Xem trailer** và thử phát/tạm dừng video.
4. **Create**: Thêm khóa học, nhập URL ảnh rồi xem trước; thử bỏ trống tên, giá âm để kiểm tra validation; lưu hợp lệ và xác nhận danh sách được đồng bộ.
5. **Edit**: Vào chi tiết khóa học vừa thêm, sửa tên, học phí, categoryId, ảnh/trailer; lưu và kiểm tra kết quả.
6. **Delete course**: Thử hủy xác nhận xóa, sau đó xác nhận xóa và kiểm tra dữ liệu biến mất khỏi danh sách.
7. **Manage categories**: Thêm danh mục, sửa thông tin, thử xóa danh mục đang có khóa học (phải bị chặn); thử xóa danh mục trống (được phép).
8. **Network/empty**: Tắt Internet để thấy thông báo lỗi; bật lại và bấm Thử lại. Với API chưa có courses, thấy trạng thái danh sách rỗng.

## 8. REST API và ví dụ JSON

Danh mục mẫu (từ `sample-data/categories.json`):

```json
{"id":"1","name":"Lập trình Mobile","description":"Android, Flutter, React Native"}
```

Khóa học mẫu (từ `sample-data/courses.json`, phần URL rút gọn):

```json
{
  "id":"1",
  "title":"Lập trình Android cơ bản với Kotlin",
  "description":"Làm quen Android Studio...",
  "categoryId":"1",
  "instructor":"Nguyễn Văn An",
  "price":500000,
  "duration":30,
  "thumbnail":"https://picsum.photos/seed/android-kotlin/1200/700",
  "trailerUrl":"https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4",
  "createdAt":"2026-10-13"
}
```

Retrofit Gson ánh xạ JSON sang `Course`/`Category`; màn hình lấy tên danh mục từ danh sách categories theo `categoryId`, **không hardcode tên danh mục**. Khi POST/PUT, app gửi `CoursePayload`/`CategoryPayload` không có `id`; MockAPI tự sinh ID khi POST.

## 9. Đề cương video thuyết trình (8–12 phút)

1. **Giới thiệu** đề tài, công nghệ, 2 resources trên MockAPI (1 phút).
2. **Cấu hình base URL** ở `gradle.properties`, diễn giải Retrofit interface và mô hình dữ liệu (2 phút).
3. **Demo** danh sách, tìm kiếm/lọc, chi tiết, tải ảnh bằng Glide và phát trailer trong app bằng Media3 (2 phút).
4. **Demo CRUD courses**: POST mới, PUT cập nhật, DELETE có xác nhận; mở MockAPI để thấy JSON thay đổi (2–3 phút).
5. **Demo CRUD categories** và kiểm tra `categoryId` trước khi DELETE, giải thích vì sao MockAPI không có khóa ngoại (1–2 phút).
6. **Validation và lỗi**: giá âm, thiếu trường, URL lỗi, mất mạng, trạng thái rỗng; nêu giới hạn khi backend chỉ là MockAPI (1–2 phút).

## 10. Những điểm cần biết

- MockAPI cần được tạo **trên tài khoản của người nộp**. Source code không thể kèm URL MockAPI thực nếu chưa được cung cấp.
- Không có database/backend tự xây, đúng giới hạn đề bài.
- Chặn xóa danh mục được thực hiện trong app. Do MockAPI không có FK hoặc transaction, nếu hai người cùng thao tác đồng thời thì vẫn có thể xảy ra race condition. Với bài tập, cách này đúng yêu cầu.
- URL có định dạng hợp lệ không đồng nghĩa máy chủ có video/ảnh hoạt động. Hãy demo với URL MP4 trực tiếp đang truy cập được.
- Không dùng YouTube watch URL cho ExoPlayer. Nếu đổi sang YouTube phải tích hợp cách phát tương thích khác.
- Mạng đang dùng HTTPS (`usesCleartextTraffic=false`) nhưng validator chấp nhận cả HTTP để thể hiện kiểm tra định dạng theo đề; với release nên giới hạn chỉ HTTPS hoặc cấu hình network security policy rõ ràng.
- Đây là source project; không có APK build sẵn hoặc video quay sẵn. Tạo APK bằng **Build > Build APK(s)** sau khi Sync thành công.

## 11. Sản phẩm cần nộp

- [x] Source code app Android Kotlin + XML
- [x] File JSON: 3 categories và 10 courses
- [x] Script tự POST dữ liệu lên MockAPI
- [x] README cấu hình/chạy/demo
- [ ] **Dán URL MockAPI thật** sau khi tạo project
- [ ] **Quay video thuyết trình** khi ứng dụng đã chạy thực tế
