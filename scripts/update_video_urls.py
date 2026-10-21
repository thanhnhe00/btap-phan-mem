import json
import urllib.request

working_videos = [
    "https://www.w3schools.com/html/mov_bbb.mp4",
    "https://interactive-examples.mdn.mozilla.net/media/cc0-videos/flower.mp4",
    "https://vjs.zencdn.net/v/oceans.mp4",
    "https://test-videos.co.uk/vids/bigbuckbunny/mp4/h264/720/Big_Buck_Bunny_720_10s_1MB.mp4",
    "https://test-videos.co.uk/vids/jellyfish/mp4/h264/720/Jellyfish_720_10s_1MB.mp4",
    "https://test-videos.co.uk/vids/sintel/mp4/h264/720/Sintel_720_10s_1MB.mp4",
    "https://media.w3.org/2010/05/bunny/trailer.mp4",
    "https://raw.githubusercontent.com/mediaelement/mediaelement-files/master/big_buck_bunny.mp4",
    "https://www.w3schools.com/html/mov_bbb.mp4",
    "https://interactive-examples.mdn.mozilla.net/media/cc0-videos/flower.mp4",
]

base = "https://6ad4d44348476c0785d94601.mockapi.io/"
courses = json.loads(urllib.request.urlopen(base + "courses").read().decode("utf-8"))
print(f"Found {len(courses)} courses.")

for idx, c in enumerate(courses):
    cid = c["id"]
    new_video = working_videos[idx % len(working_videos)]
    put_data = {
        "title": c["title"],
        "description": c["description"],
        "categoryId": c["categoryId"],
        "instructor": c["instructor"],
        "price": c["price"],
        "duration": c["duration"],
        "thumbnail": c["thumbnail"],
        "trailerUrl": new_video,
        "createdAt": c.get("createdAt", "2026-10-13"),
    }
    put_req = urllib.request.Request(
        f"{base}courses/{cid}",
        data=json.dumps(put_data).encode("utf-8"),
        headers={"Content-Type": "application/json", "Accept": "application/json"},
        method="PUT",
    )
    with urllib.request.urlopen(put_req) as res:
        print(f"Updated Course {cid} ({c['title']}): status {res.status}")

print("All courses successfully updated on MockAPI!")
