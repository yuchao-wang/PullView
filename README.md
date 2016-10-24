
## 下拉刷新类库 PullViewLibrary 包含两部分
### 1. PullView相关控件
- PullListView
- PullGridView
- PullScrollView
- PullWebView

### 2. BaseAdapter For ListView 的进一步封装

#### 2.1 功能
- 数据源封装
- 增删改查
- 局部刷新

#### 2.2 用法
- 简单用法：不复用子布局（showItemView使用ViewHolder）
- 高级用法：复用子布局（showItemView不使用ViewHolder）
- 同一个列表，不同布局，子类可重写getItemType，子类也可在initView中判断

### Dependence 

```
compile 'wang.yuchao.android.library.view.pullviewlibrary:PullViewLibrary:1.0.0'
```

## [About Me](http://yuchao.wang)


## License

```
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```