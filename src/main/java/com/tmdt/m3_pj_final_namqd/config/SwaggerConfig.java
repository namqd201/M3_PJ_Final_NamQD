package com.tmdt.m3_pj_final_namqd.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    /** Thứ tự tag trong OpenAPI (Swagger UI dùng thứ tự này khi tags-sorter để trống). */
    public static final List<Tag> API_TAGS = List.of(
            new Tag().name("01. Authentication").description("Đăng nhập (PUBLIC), JWT; hồ sơ người dùng hiện tại"),
            new Tag().name("02. User management").description("Quản lý tài khoản — chỉ ADMIN"),
            new Tag().name("03. Student management").description("Sinh viên — ADMIN/MENTOR/STUDENT (theo quy tắc lọc)"),
            new Tag().name("04. Mentor management").description("Mentor — ADMIN/STUDENT/MENTOR (theo quy tắc lọc)"),
            new Tag().name("05. Internship phases").description("Đợt thực tập — xem: mọi vai trò đăng nhập; thay đổi: ADMIN"),
            new Tag().name("06. Evaluation criteria").description("Tiêu chí đánh giá — xem: mọi vai trò; CRUD: ADMIN"),
            new Tag().name("07. Assessment rounds").description("Đợt đánh giá — xem: mọi vai trò; CRUD: ADMIN"),
            new Tag().name("08. Round criteria").description("Tiêu chí trong đợt — xem: mọi vai trò; thay đổi: ADMIN"),
            new Tag().name("09. Internship assignments").description("Phân công thực tập — xem: theo vai trò; tạo/cập nhật trạng thái: ADMIN"),
            new Tag().name("10. Assessment results").description("Kết quả đánh giá — xem: theo vai trò; tạo/sửa: MENTOR (được phân công)")
    );

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Internship Management API")
                        .version("1.0")
                        .description("API quản lý thực tập"))
                .tags(API_TAGS)
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
