#!/usr/bin/env python3
"""Tạo 3 categories và 10 courses trên MockAPI, không cần thư viện ngoài.

Chạy: python scripts/seed_mockapi.py https://xxxxxxxx.mockapi.io/api/v1/
Script không sửa/xóa dữ liệu đã có. Khóa học cũ trùng title được bỏ qua.
"""
import json
import sys
from pathlib import Path
from urllib.error import HTTPError, URLError
from urllib.parse import urlparse
from urllib.request import Request, urlopen

ROOT = Path(__file__).resolve().parents[1]


def request_api(base: str, resource: str, method: str = "GET", payload=None):
    body = None if payload is None else json.dumps(payload, ensure_ascii=False).encode("utf-8")
    req = Request(
        base + resource,
        data=body,
        method=method,
        headers={"Content-Type": "application/json", "Accept": "application/json"},
    )
    with urlopen(req, timeout=20) as response:
        data = response.read().decode("utf-8")
        return json.loads(data) if data else None


def main():
    if len(sys.argv) != 2:
        sys.exit("Cách chạy: python scripts/seed_mockapi.py https://<id>.mockapi.io/api/v1/")
    base = sys.argv[1].rstrip("/") + "/"
    parsed = urlparse(base)
    if parsed.scheme != "https" or not parsed.hostname or not parsed.hostname.endswith(".mockapi.io"):
        sys.exit("Chỉ chấp nhận URL HTTPS thuộc domain *.mockapi.io để tránh POST nhầm server.")

    sample_categories = json.loads((ROOT / "sample-data/categories.json").read_text(encoding="utf-8"))
    sample_courses = json.loads((ROOT / "sample-data/courses.json").read_text(encoding="utf-8"))
    try:
        existing_categories = request_api(base, "categories")
        existing_courses = request_api(base, "courses")
        if not isinstance(existing_categories, list) or not isinstance(existing_courses, list):
            sys.exit("Phản hồi không phải danh sách; kiểm tra lại API resources.")
        id_mapping = {}
        added_categories = 0
        for template in sample_categories:
            same_name = next((x for x in existing_categories if x.get("name") == template["name"]), None)
            if same_name is None:
                body = {k: v for k, v in template.items() if k != "id"}
                same_name = request_api(base, "categories", "POST", body)
                existing_categories.append(same_name)
                added_categories += 1
                print("+ Danh mục:", same_name["name"])
            id_mapping[template["id"]] = str(same_name["id"])

        added_courses = 0
        for template in sample_courses:
            if any(x.get("title") == template["title"] for x in existing_courses):
                print("= Đã có khóa học:", template["title"])
                continue
            body = {k: v for k, v in template.items() if k != "id"}
            body["categoryId"] = id_mapping[template["categoryId"]]
            created = request_api(base, "courses", "POST", body)
            existing_courses.append(created)
            added_courses += 1
            print("+ Khóa học:", created["title"])
        print(f"\nHoàn tất: thêm {added_categories} danh mục, {added_courses} khóa học.")
        print(f"MockAPI hiện có {len(existing_categories)} danh mục, {len(existing_courses)} khóa học.")
    except HTTPError as error:
        sys.exit(f"HTTP {error.code} khi gọi {error.url}. Kiểm tra project, resources và schema.")
    except (URLError, TimeoutError) as error:
        sys.exit(f"Không kết nối được MockAPI: {error}")


if __name__ == "__main__":
    main()
