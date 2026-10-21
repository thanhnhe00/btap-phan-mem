package com.example.coursemanagement.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CourseValidatorTest {
    private fun check(
        title: String = "Lập trình Android",
        price: String = "500000",
        category: String = "1",
        trailer: String = "https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
    ) = CourseValidator.validate(
        title, "Lập trình Kotlin", "Giảng viên A", price, "30",
        "https://picsum.photos/seed/course/1200/700", trailer, category, setOf("1", "2")
    )

    @Test fun validCourse() = assertNull(check())
    @Test fun blankTitle() = assertEquals("Tên khóa học không được để trống.", check(title = ""))
    @Test fun priceCannotBeNegative() = assertEquals("Học phí phải là số không âm.", check(price = "-1"))
    @Test fun priceCannotBeNaN() = assertEquals("Học phí phải là số không âm.", check(price = "NaN"))
    @Test fun rejectsUnknownCategory() = assertEquals(
        "Vui lòng chọn danh mục hợp lệ từ MockAPI.", check(category = "999")
    )
    @Test fun acceptsZeroPrice() = assertNull(check(price = "0"))
    @Test fun urlChecks() {
        assertTrue(CourseValidator.isHttpUrl("https://example.com/video.mp4"))
        assertFalse(CourseValidator.isHttpUrl("file:///sdcard/movie.mp4"))
        assertFalse(CourseValidator.isHttpUrl("javascript:alert(1)"))
    }
}
