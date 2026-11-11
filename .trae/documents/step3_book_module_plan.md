# 第 3 步：商品模块实施计划

## Repository Research

**现有状态：**
- 18 个 Java 文件，覆盖 common / entity / repository / service / controller / config / dto
- 3 个 Thymeleaf 模板：index / login / register
- pom.xml 已禁用 Lombok（JDK 26 不兼容），手写 getter/setter
- category 表已建（8 条初始分类数据），book 表已建（含 stock 字段）
- WebMvcConfig 已预留 `/book/publish` 拦截和 `/book`、`/book/detail` 放行
- PageHelper、MyBatis 驼峰映射、Result<T>/PageResult<T>/PageQuery 均已就绪

**本次目标：** 商品发布、商品搜索浏览（分页 + 关键字 + 分类筛选）、商品详情

## Files and Modules

**新增文件：**
| 路径 | 说明 |
|------|------|
| `entity/Category.java` | 分类实体 |
| `entity/Book.java` | 商品实体（含 sellerName 冗余字段用于详情页直接展示卖家名） |
| `dto/BookQuery.java` | 商品列表查询 DTO，继承 PageQuery |
| `dto/PublishBookDTO.java` | 发布商品请求 DTO，带 @Valid 校验 |
| `repository/CategoryMapper.java` | 分类 Mapper 接口 |
| `repository/BookMapper.java` | 商品 Mapper 接口 |
| `resources/mapper/CategoryMapper.xml` | 分类 XML |
| `resources/mapper/BookMapper.xml` | 商品 XML（动态 SQL 多条件查询 + 浏览次数 +1） |
| `service/CategoryService.java` | 分类服务接口 |
| `service/BookService.java` | 商品服务接口 |
| `service/impl/CategoryServiceImpl.java` | 分类服务实现 |
| `service/impl/BookServiceImpl.java` | 商品服务实现 |
| `controller/BookController.java` | 商品 API 控制器（列表/详情/发布） |
| `controller/PageController.java`（更新） | 新增 /book、/book/detail、/book/publish 页面路由 |
| `templates/book-list.html` | 商品列表页（搜索框 + 分类 Tab + 分页） |
| `templates/book-detail.html` | 商品详情页 |
| `templates/book-publish.html` | 发布商品页（登录用户） |

**更新文件：**
| 路径 | 说明 |
|------|------|
| `config/WebMvcConfig.java` | 加 `/book/publish/submit` 到拦截路径 |
| `resources/templates/index.html` | 顶部导航加"商品"入口 |

## Implementation Steps

### 1. 基础层：Category 实体 + Mapper + Service
- Category 实体字段：id / name / sort / createTime
- CategoryMapper：selectAllBySort（按 sort 排序查全部分类）
- CategoryService：getAll() 单一方法

### 2. 商品实体 + DTO
- Book 实体：完全对应 book 表所有字段 + sellerName（冗余，MyBatis resultMap 关联查）
- BookQuery 继承 PageQuery：keyword（模糊搜索 title/author）、categoryId、status（默认只查上架=1）
- PublishBookDTO：title/@NotBlank、author、publisher、isbn、categoryId、originalPrice、price/@NotNull、condition、description、coverImage，无 sellerId（从 Session 取）

### 3. BookMapper + XML（考核重点：动态 SQL）
- selectWithCondition：<where> + <if> 动态拼装 keyword（LIKE CONCAT('%',keyword,'%')）、categoryId（=）、status（=）
- selectByIdWithSeller：LEFT JOIN user ON book.seller_id = user.id，回填 sellerName
- updateViewCount：浏览次数 +1（单独 SQL，不走通用 updateById）
- insert：useGeneratedKeys 回填自增主键
- selectBySellerId：我发布的商品列表（个人中心用，提前写）

### 4. BookService + Impl
- publish(Long sellerId, PublishBookDTO dto)：构造 Book → insert → 返回
- getPage(BookQuery query)：PageHelper.startPage → mapper.selectWithCondition → 包装 PageResult
- getDetail(Long id)：先 updateViewCount +1 → 再 selectByIdWithSeller
- getBySeller(Long sellerId, BookQuery query)：分页查卖家的商品

### 5. BookController
- API：`GET /api/category/list` → 所有分类（公开）
- API：`GET /api/book/list` → 分页列表（keyword / categoryId / pageNum / pageSize，公开）
- API：`GET /api/book/detail` → 详情（公开，view_count +1）
- API：`POST /api/book/publish` → 发布（需登录，sellerId 从 Session 取）
- 页面路由：`GET /book` → 列表页，`GET /book/detail/{id}` → 详情页，`GET /book/publish` → 发布页

### 6. Thymeleaf 模板
- book-list.html：Bootstrap 卡片网格 + 搜索框 + 分类下拉 + 页码导航，顶部导航栏复用首页结构（th:if 判断登录态）
- book-detail.html：封面图 + 完整字段 + 卖家信息 + "立即购买"按钮 + "收藏"按钮
- book-publish.html：表单 + 分类下拉选项（th:each 遍历分类）

### 7. 更新现有文件
- index.html 顶部导航加"商品"链接
- WebMvcConfig：在 addPathPatterns 加 `/api/book/publish`（页面已拦，API 也需拦）

## Dependencies and Considerations

- **浏览次数 +1**：MyBatis 的 `update view_count = view_count + 1 where id = #{id}` 必须走单独 SQL，不能通过 MyBatis-Plus 通用 update（本项目没用 MP，所以直接写 XML）
- **动态 SQL**：keyword 搜索需同时匹配 title 和 author，用 OR 连接，外面包 <where>
- **sellerName 关联查询**：XML 用 LEFT JOIN user，resultMap 用 association 或手写 result 映射，sellerName 是 Book 实体的非表字段
- **分页逻辑**：PageHelper.startPage 必须紧跟在 mapper 调用之前，中间不能有其他查询
- **图片**：cover_image 暂不做文件上传，发布表单用 URL 文本输入框；封面图统一用 CDN 占位图（https://via.placeholder.com/200x260?text=No+Cover）

## Validation

1. `mvn clean compile` 编译无错误
2. 启动应用：`GET /api/category/list` 返回 8 条分类
3. 发布商品：登录 seller01 → `POST /api/book/publish` 返回 id
4. 列表搜索：`GET /api/book/list?keyword=计算机` 返回分页结果
5. 详情页：`GET /book/detail/{id}` 渲染正确，view_count +1
6. 分类筛选：`GET /api/book/list?categoryId=2` 只查计算机类
7. 未登录拦截：直接 `POST /api/book/publish` 返回 401

## Risks

- **Book 实体验证**：sellerName 非表字段，MyBatis 不会自动映射，需 resultMap 手动配 `<result column="nickname" property="sellerName"/>`；updateById 要排除 sellerName（<if> 动态 SQL 自动跳过 null 字段，无风险）
- **PageHelper 与多查询**：详情页 updateViewCount 和 selectByIdWithSeller 是两次查询，不会触发 PageHelper 二次分页；列表页 PageHelper 只包一次 mapper 调用
- **分类下拉空值**：发布页面如果没传 categoryId，页面会渲染空选项；CategoryMapper.selectAllBySort 若返回空列表，需要前端显示"请先配置分类"提示（实际数据库有 8 条，无风险）
