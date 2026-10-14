放置底部 TabBar 图标（6 张 png，每张建议 81x81 透明背景）：

- home.png / home-active.png
- workbench.png / workbench-active.png
- me.png / me-active.png

如果不方便放图片，编辑 `src/pages.json` 把 `iconPath`/`selectedIconPath` 两项删掉，
只保留 `pagePath` + `text` 也能正常显示。
