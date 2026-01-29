package com.fpt.glassesshop.config;

import com.fpt.glassesshop.entity.*;
import com.fpt.glassesshop.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataInitialize implements CommandLineRunner {

        private final UserAccountRepository userAccountRepository;
        private final ProductRepository productRepository;
        private final ProductVariantRepository productVariantRepository;
        private final LensOptionRepository lensOptionRepository;
        private final PromotionRepository promotionRepository;

        @Override
        public void run(String... args) throws Exception {
                if (userAccountRepository.count() == 0) {
                        seedUsers();
                }
                if (productRepository.count() == 0) {
                        seedProducts();
                }
                if (lensOptionRepository.count() == 0) {
                        seedLensOptions();
                }
                if (promotionRepository.count() == 0) {
                        seedPromotions();
                }
        }

        private void seedUsers() {
                UserAccount admin = UserAccount.builder()
                                .name("Admin User")
                                .email("admin@glassesshop.com")
                                .phone("1234567890")
                                .role("ADMIN")
                                .passwordHash("hashed_password_admin") // In real app, use BCrypt
                                .accountStatus("ACTIVE")
                                .build();

                UserAccount customer = UserAccount.builder()
                                .name("John Doe")
                                .email("john.doe@example.com")
                                .phone("0987654321")
                                .role("CUSTOMER")
                                .passwordHash("hashed_password_john")
                                .accountStatus("ACTIVE")
                                .build();

                userAccountRepository.saveAll(List.of(admin, customer));
        }

        private void seedProducts() {
                // Product 1: Classic Aviator
                Product aviator = Product.builder()
                                .name("Classic Aviator")
                                .brand("Ray-Ban")
                                .description("Timeless aviator style sunglasses.")
                                .productType(Product.ProductType.FRAME)
                                .isPrescriptionSupported(true)
                                .build();
                productRepository.save(aviator);

                ProductVariant aviatorGold = ProductVariant.builder()
                                .product(aviator)
                                .price(new BigDecimal("150.00"))
                                .stockQuantity(100)
                                .frameSize("Medium")
                                .color("Gold")
                                .material("Metal")
                                .imageUrl("https://example.com/aviator-gold.jpg")
                                .status("AVAILABLE")
                                .build();

                ProductVariant aviatorBlack = ProductVariant.builder()
                                .product(aviator)
                                .price(new BigDecimal("145.00"))
                                .stockQuantity(50)
                                .frameSize("Large")
                                .color("Black")
                                .material("Metal")
                                .imageUrl("https://example.com/aviator-black.jpg")
                                .status("AVAILABLE")
                                .build();

                productVariantRepository.saveAll(List.of(aviatorGold, aviatorBlack));

                // Product 2: Reading Glasses
                Product readingGlasses = Product.builder()
                                .name("Modern Reader")
                                .brand("Generic")
                                .description("Simple and elegant reading glasses.")
                                .productType(Product.ProductType.FRAME)
                                .isPrescriptionSupported(true)
                                .build();
                productRepository.save(readingGlasses);

                ProductVariant readerBlue = ProductVariant.builder()
                                .product(readingGlasses)
                                .price(new BigDecimal("25.00"))
                                .stockQuantity(200)
                                .frameSize("Small")
                                .color("Blue")
                                .material("Plastic")
                                .imageUrl("https://example.com/reader-blue.jpg")
                                .status("AVAILABLE")
                                .build();
                productVariantRepository.save(readerBlue);

                // Product 3: Cleaning Kit
                Product cleaningKit = Product.builder()
                                .name("Lens Cleaning Kit")
                                .brand("ClearView")
                                .description("Microfiber cloth and spray.")
                                .productType(Product.ProductType.ACCESSORY)
                                .isPrescriptionSupported(false)
                                .build();
                productRepository.save(cleaningKit);

                ProductVariant kitStandard = ProductVariant.builder()
                                .product(cleaningKit)
                                .price(new BigDecimal("10.00"))
                                .stockQuantity(500)
                                .color("N/A")
                                .material("Liquid/Cloth")
                                .imageUrl("https://example.com/cleaning-kit.jpg")
                                .status("AVAILABLE")
                                .build();
                productVariantRepository.save(kitStandard);

        }

        private void seedLensOptions() {
                LensOption singleVision = LensOption.builder()
                                .type("Single Vision")
                                .thickness("1.50 Standard")
                                .coating("Anti-Reflective")
                                .color("Clear")
                                .price(new BigDecimal("30.00"))
                                .build();

                LensOption highIndex = LensOption.builder()
                                .type("High Index")
                                .thickness("1.67 Thin")
                                .coating("Blue Light Filter")
                                .color("Clear")
                                .price(new BigDecimal("80.00"))
                                .build();

                LensOption photochromic = LensOption.builder()
                                .type("Photochromic")
                                .thickness("1.59 Polycarbonate")
                                .coating("Scratch Resistant")
                                .color("Transition Grey")
                                .price(new BigDecimal("100.00"))
                                .build();

                lensOptionRepository.saveAll(List.of(singleVision, highIndex, photochromic));
        }

        private void seedPromotions() {
                Promotion welcomePromo = Promotion.builder()
                                .code("WELCOME20")
                                .description("Welcome discount for new members")
                                .discountType(Promotion.DiscountType.PERCENTAGE)
                                .discountValue(new BigDecimal("20.00"))
                                .startDate(LocalDateTime.now())
                                .endDate(LocalDateTime.now().plusMonths(1))
                                .isActive(true)
                                .build();

                promotionRepository.save(welcomePromo);
        }
}
