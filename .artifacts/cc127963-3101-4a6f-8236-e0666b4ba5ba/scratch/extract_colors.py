from PIL import Image
import os

img_path = r"C:/Users/user/AndroidStudioProjects/BMateMatch/app/src/main/res/drawable/logo.png"
if os.path.exists(img_path):
    img = Image.open(img_path).convert("RGB")
    # Resize for faster processing
    img = img.resize((100, 100))
    colors = img.getcolors(10000)
    # Sort by count
    colors.sort(key=lambda x: x[0], reverse=True)
    print("Top dominant colors (count, RGB):")
    for count, rgb in colors[:10]:
        hex_val = '#%02x%02x%02x' % rgb
        print(f"Count: {count}, RGB: {rgb}, Hex: {hex_val}")
else:
    print("Logo not found at", img_path)
