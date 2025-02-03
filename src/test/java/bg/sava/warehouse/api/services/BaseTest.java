package bg.sava.warehouse.api.services;

import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseTest {

    @Container
    protected static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:latest")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @BeforeAll
    static void startContainers() {
        if (!mysqlContainer.isRunning()) {
            mysqlContainer.start();
        }
    }

    private static final EasyRandom easyRandom;

    static  {
        EasyRandomParameters parameters = new EasyRandomParameters()
                .objectPoolSize(100)
                .randomizationDepth(3)
                .stringLengthRange(5, 19)
                .dateRange(LocalDate.of(2023, 1, 1), LocalDate.of(2024, 12, 31))
                .collectionSizeRange(1, 10);
        easyRandom = new EasyRandom(parameters);
    }

    public EasyRandom getEasyRandom() {
        return easyRandom;
    }
}
