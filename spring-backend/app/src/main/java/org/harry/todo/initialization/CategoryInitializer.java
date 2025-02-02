package org.harry.todo.initialization;

import org.harry.todo.entities.Category;
import org.harry.todo.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class CategoryInitializer implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryInitializer.class);

    @Autowired
    private CategoryRepository categoryRepository;


    @Override
    public void run(String... args) {
        try{
            if (categoryRepository.count() == 0){
                List<Category> defaultCategories = Arrays.asList(
                        makeCategory(1, "Work"),
                        makeCategory(2, "Personal"),
                        makeCategory(3, "Home"),
                        makeCategory(4, "Shopping"),
                        makeCategory(5, "Health"),
                        makeCategory(6, "Education"),
                        makeCategory(7, "Family"),
                        makeCategory(8, "Finance"),
                        makeCategory(9, "Social"),
                        makeCategory(10, "Projects"),
                        makeCategory(11, "Travel"),
                        makeCategory(12, "Important"),
                        makeCategory(13, "Ideas"),
                        makeCategory(14, "Others")
                );

                categoryRepository.saveAll(defaultCategories);
                LOGGER.info("Successfully initialize {} default categories", defaultCategories.size());
            } else {
                LOGGER.debug("Category table is not empty, skipping initialization");
            }
        } catch (Exception e) {
            LOGGER.error("Error initializing the category {}", e.getMessage());
        }
    }

    private Category makeCategory(Integer id,String name){
        return Category.builder()
                .categoryId(id)
                .categoryName(name)
                .build();
    }
}
