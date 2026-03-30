package com.fpt.glasseshop.config;

import com.fpt.glasseshop.entity.*;
import com.fpt.glasseshop.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.JdbcTemplate;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
@Slf4j
public class


DataInitialize implements CommandLineRunner {

        private final UserAccountRepository userAccountRepository;
        private final ProductRepository productRepository;
        private final ProductVariantRepository productVariantRepository;
        private final LensOptionRepository lensOptionRepository;
        private final PromotionRepository promotionRepository;
        private final OrderRepository orderRepository;
        private final OrderItemRepository orderItemRepository;
        private final AddressRepository addressRepository;
        private final PrescriptionRepository prescriptionRepository;
        private final PreOrderRepository preOrderRepository;
        private final JdbcTemplate jdbcTemplate;
        private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

        @Override
        public void run(String... args) throws Exception {
                log.info("Ensuring database schema is up-to-date...");
                try {
                    // Tự động thêm cột is_preorder vào bảng order_item nếu chưa có (H2 or MySQL syntax)
                    jdbcTemplate.execute("ALTER TABLE order_item ADD COLUMN IF NOT EXISTS is_preorder BOOLEAN DEFAULT FALSE");
                    log.info("Database schema check: 'is_preorder' column verified.");
                } catch (Exception e) {
                    log.warn("Could not add 'is_preorder' column automatically (it might already exist). Detail: {}", e.getMessage());
                }

                log.info("Checking data initialization status...");
                if (userAccountRepository.count() == 0) {
                        log.info("Seeding users...");
                        seedUsers();
                }
                if (productRepository.count() == 0) {
                        log.info("Seeding products...");
                        seedProducts();
                }
                if (lensOptionRepository.count() == 0) {
                        log.info("Seeding lens options...");
                        seedLensOptions();
                }
                if (promotionRepository.count() == 0) {
                        log.info("Seeding promotions...");
                        seedPromotions();
                }
                if (orderRepository.count() == 0) {
                        log.info("Seeding orders...");
                        seedOrders();
                }
                log.info("Data initialization check complete. Product count: {}", productRepository.count());
        }

        private void seedUsers() {
                UserAccount admin = UserAccount.builder()
                                .name("Admin User")
                                .email("admin@example.com")
                                .phone("1234567890")
                                .role("ADMIN")
                                .passwordHash(passwordEncoder.encode("admin123"))
                                .accountStatus("ACTIVE")
                                .build();

                UserAccount staff = UserAccount.builder()
                                .name("Staff User")
                                .email("staff@example.com")
                                .phone("1122334455")
                                .role("OPERATIONAL_STAFF")
                                .passwordHash(passwordEncoder.encode("staff123"))
                                .accountStatus("ACTIVE")
                                .build();

                UserAccount customer = UserAccount.builder()
                                .name("John Doe")
                                .email("john.doe@example.com")
                                .phone("0987654321")
                                .role("CUSTOMER")
                                .passwordHash(passwordEncoder.encode("customer123"))
                                .accountStatus("ACTIVE")
                                .build();

                userAccountRepository.saveAll(Arrays.asList(admin, staff, customer));
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
                                .stockQuantity(100)
                                .frameSize("Medium")
                                .color("Gold")
                                .material("Metal")
                                .imageUrl("https://rocketeyewear.com/cdn/shop/files/RocketEyewear-240907-46.png?v=1728279234")
                                .status("AVAILABLE")
                                .active(true)
                                .deleted(false)
                                .build();

                ProductVariant aviatorBlack = ProductVariant.builder()
                                .product(aviator)
                                .stockQuantity(50)
                                .frameSize("Large")
                                .color("Black")
                                .material("Metal")
                                .imageUrl("https://assets2.glasses.com/cdn-record-files-pi/9f81c8ee-571c-4e24-aa12-a358001ddb43/c12584a8-28ee-4a99-8ba6-ad33014b4b22/0RX5154__2000__STD__shad__qt.png?impolicy=GL_parameters_transp_clone1440")
                                .status("AVAILABLE")
                                .active(true)
                                .deleted(false)
                                .build();

                productVariantRepository.saveAll(Arrays.asList(aviatorGold, aviatorBlack));

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
                                .stockQuantity(200)
                                .frameSize("Small")
                                .color("Blue")
                                .material("Plastic")
                                .imageUrl("https://static.zennioptical.com/production/products/general/44/67/4467121-eyeglasses-angle-view.jpg")
                                .status("AVAILABLE")
                                .active(true)
                                .deleted(false)
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
                                .stockQuantity(500)
                                .color("N/A")
                                .material("Liquid/Cloth")
                                .imageUrl("https://rocketeyewear.com/cdn/shop/files/RocketEyewear-240907-46.png?v=1728279234")
                                .status("AVAILABLE")
                                .active(true)
                                .deleted(false)
                                .build();
                productVariantRepository.save(kitStandard);

                // Product 4: Kính Wayfarer (Gọng nhựa dày cổ điển)
                Product wayfarer = Product.builder()
                        .name("Wayfarer Classic")
                        .brand("Ray-Ban")
                        .description("Classic thick plastic frame, suitable for all face shapes.")
                        .productType(Product.ProductType.FRAME)
                        .isPrescriptionSupported(true)
                        .build();
                productRepository.save(wayfarer);

                ProductVariant wayfarerBlack = ProductVariant.builder()
                        .product(wayfarer)
                        .stockQuantity(120)
                        .frameSize("Medium")
                        .color("Matte Black")
                        .material("Acetate")
                        .imageUrl("https://static.zennioptical.com/production/products/general/20/18/2018621-eyeglasses-angle-view.jpg")
                        .status("AVAILABLE")
                        .active(true)
                        .deleted(false)
                        .build();

                ProductVariant wayfarerTortoise = ProductVariant.builder()
                        .product(wayfarer)
                        .stockQuantity(80)
                        .frameSize("Medium")
                        .color("Tortoise")
                        .material("Acetate")
                        .imageUrl("https://static.zennioptical.com/production/products/general/20/18/2018625-eyeglasses-angle-view.jpg")
                        .status("AVAILABLE")
                        .active(true)
                        .deleted(false)
                        .build();
                productVariantRepository.saveAll(Arrays.asList(wayfarerBlack, wayfarerTortoise));

                // Product 5: Kính Clubmaster (Nửa viền thanh lịch)
                Product clubmaster = Product.builder()
                        .name("Clubmaster Elegant")
                        .brand("Armani")
                        .description("Elegant half-rim glasses with a professional style.")
                        .productType(Product.ProductType.FRAME)
                        .isPrescriptionSupported(true)
                        .build();
                productRepository.save(clubmaster);

                ProductVariant clubmasterSilver = ProductVariant.builder()
                        .product(clubmaster)
                        .stockQuantity(60)
                        .frameSize("Medium")
                        .color("Black/Silver")
                        .material("Mixed")
                        .imageUrl("https://static.zennioptical.com/production/products/general/19/54/195421-eyeglasses-angle-view.jpg")
                        .status("AVAILABLE")
                        .active(true)
                        .deleted(false)
                        .build();
                productVariantRepository.save(clubmasterSilver);

                // Product 6: Kính tròn Vintage (Phong cách Hàn Quốc)
                Product vintageRound = Product.builder()
                        .name("Vintage Round Metal")
                        .brand("Vogue")
                        .description("Lightweight retro metal frame, Korean style.")
                        .productType(Product.ProductType.FRAME)
                        .isPrescriptionSupported(true)
                        .build();
                productRepository.save(vintageRound);

                ProductVariant roundGold = ProductVariant.builder()
                        .product(vintageRound)
                        .stockQuantity(150)
                        .frameSize("Small")
                        .color("Rose Gold")
                        .material("Titanium")
                        .imageUrl("https://static.zennioptical.com/production/products/general/11/25/1125214-eyeglasses-angle-view.jpg")
                        .status("AVAILABLE")
                        .active(true)
                        .deleted(false)
                        .build();

                ProductVariant roundSilver = ProductVariant.builder()
                        .product(vintageRound)
                        .stockQuantity(100)
                        .frameSize("Small")
                        .color("Silver")
                        .material("Titanium")
                        .imageUrl("https://static.zennioptical.com/production/products/general/15/78/157811-eyeglasses-angle-view.jpg")
                        .status("AVAILABLE")
                        .active(true)
                        .deleted(false)
                        .build();
                productVariantRepository.saveAll(Arrays.asList(roundGold, roundSilver));

                // Product 7: Kính Cat-eye (Mắt mèo cho nữ)
                Product catEye = Product.builder()
                        .name("Cat-Eye Glamour")
                        .brand("Prada")
                        .description("Trendy cat-eye glasses that flatter feminine facial features.")
                        .productType(Product.ProductType.FRAME)
                        .isPrescriptionSupported(true)
                        .build();
                productRepository.save(catEye);

                ProductVariant catEyeRed = ProductVariant.builder()
                        .product(catEye)
                        .stockQuantity(40)
                        .frameSize("Medium")
                        .color("Burgundy")
                        .material("Acetate")
                        .imageUrl("https://static.zennioptical.com/production/products/general/20/20/2020118-eyeglasses-angle-view.jpg")
                        .status("AVAILABLE")
                        .active(true)
                        .deleted(false)
                        .build();
                productVariantRepository.save(catEyeRed);

                // Product 8: Kính thể thao (Bản to ôm mặt)
                Product sportGlasses = Product.builder()
                        .name("Pro Sport Wrap")
                        .brand("Oakley")
                        .description("Wrap-around sports glasses, anti-slip for active movements.")
                        .productType(Product.ProductType.FRAME)
                        .isPrescriptionSupported(true)
                        .build();
                productRepository.save(sportGlasses);

                ProductVariant sportBlue = ProductVariant.builder()
                        .product(sportGlasses)
                        .stockQuantity(70)
                        .frameSize("Large")
                        .color("Blue/Black")
                        .material("TR90")
                        .imageUrl("https://static.zennioptical.com/production/products/general/70/88/708816-eyeglasses-angle-view.jpg")
                        .status("AVAILABLE")
                        .active(true)
                        .deleted(false)
                        .build();
                productVariantRepository.save(sportBlue);

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

                lensOptionRepository.saveAll(Arrays.asList(singleVision, highIndex, photochromic));
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

        private void seedOrders() {
                UserAccount customer = userAccountRepository.findByEmail("john.doe@example.com").orElse(null);
                if (customer == null)
                        return;

                Address address = Address.builder()
                                .user(customer)
                                .street("123 Main St")
                                .city("Metropolis")
                                .country("Sampleland")
                                .build();
                addressRepository.save(address);

                ProductVariant aviatorGold = productVariantRepository.findAll().get(0);
                LensOption singleVision = lensOptionRepository.findAll().get(0);

                // 1. Prescription Order
                Order o1 = Order.builder()
                                .user(customer)
                                .status("PENDING")
                                .totalPrice(new BigDecimal("180.00"))
                                .shippingAddress(address)
                                .billingAddress(address)
                                .paymentStatus("PAID")
                        .orderCode("ORD-1234567890")
                                .build();
                orderRepository.save(o1);

                OrderItem i1 = OrderItem.builder()
                                .order(o1)
                                .variant(aviatorGold)
                                .variantId(aviatorGold.getVariantId())
                                .productId(aviatorGold.getProduct().getProductId())
                                .productName(aviatorGold.getProduct().getName())
                                .variantColor(aviatorGold.getColor())
                                .variantSize(aviatorGold.getFrameSize())
                                .imageUrl(aviatorGold.getImageUrl())
                                .lensOption(singleVision)
                                .lensOptionId(singleVision.getLensOptionId())
                                .lensType(singleVision.getType())
                                .lensPrice(singleVision.getPrice())
                                .lensCoating(singleVision.getCoating())
                                .quantity(1)
                                .unitPrice(new BigDecimal("180.00"))
                                .fulfillmentType("PRESCRIPTION")
                                .build();
                orderItemRepository.save(i1);

                Prescription p1 = Prescription.builder()
                                .orderItem(i1)
                                .doctorName("Dr. Smith")
                                .status(true)
                                .sphLeft(new BigDecimal("-1.50"))
                                .sphRight(new BigDecimal("-1.75"))
                                .pd(new BigDecimal("63.0"))
                                .createdAt(LocalDateTime.now())
                                .build();
                prescriptionRepository.save(p1);

                // 2. In-Stock Order
                Order o2 = Order.builder()
                                .user(customer)
                                .status("SHIPPED")
                                .totalPrice(new BigDecimal("150.00"))
                                .shippingAddress(address)
                                .billingAddress(address)
                                .paymentStatus("PAID")
                        .orderCode("ORD-98765432101")
                                .build();
                orderRepository.save(o2);

                OrderItem i2 = OrderItem.builder()
                                .order(o2)
                                .variant(aviatorGold)
                                .variantId(aviatorGold.getVariantId())
                                .productId(aviatorGold.getProduct().getProductId())
                                .productName(aviatorGold.getProduct().getName())
                                .variantColor(aviatorGold.getColor())
                                .variantSize(aviatorGold.getFrameSize())
                                .imageUrl(aviatorGold.getImageUrl())
                                .quantity(1)
                                .unitPrice(new BigDecimal("150.00"))
                                .fulfillmentType("IN_STOCK")
                                .build();
                orderItemRepository.save(i2);

                // 3. Pre-Order
                Order o3 = Order.builder()
                                .user(customer)
                                .status("PENDING")
                                .totalPrice(new BigDecimal("200.00"))
                                .shippingAddress(address)
                                .billingAddress(address)
                                .paymentStatus("PAID")
                        .orderCode("ORD-12345678903")
                                .build();
                orderRepository.save(o3);

                OrderItem i3 = OrderItem.builder()
                                .order(o3)
                                .variant(aviatorGold)
                                .variantId(aviatorGold.getVariantId())
                                .productId(aviatorGold.getProduct().getProductId())
                                .productName(aviatorGold.getProduct().getName())
                                .variantColor(aviatorGold.getColor())
                                .variantSize(aviatorGold.getFrameSize())
                                .imageUrl(aviatorGold.getImageUrl())
                                .quantity(1)
                                .unitPrice(new BigDecimal("200.00"))
                                .fulfillmentType("PRE_ORDER")
                                .build();
                orderItemRepository.save(i3);

                PreOrder po1 = PreOrder.builder()
                                .orderItem(i3)
                                .status("ORDERED_FROM_SUPPLIER")
                                .supplierName("Global Optics")
                                .expectedArrival(java.time.LocalDate.now().plusDays(14))
                                .build();
                preOrderRepository.save(po1);
        }
}
