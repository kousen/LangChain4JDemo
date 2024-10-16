package com.kousenit;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class PromptTemplateTest {
    private final ChatLanguageModel model = AiModels.GPT_4_O;

    @Test
    void promptTemplateWithParameters() {
        PromptTemplate promptTemplate =
                PromptTemplate.from("Say '{{text}}' in {{language}}.");
        Prompt prompt = promptTemplate.apply(
                Map.ofEntries(
                        Map.entry("text", "hi"),
                        Map.entry("language", "German")));
        assertThat(prompt.text()).isEqualTo("Say 'hi' in German.");
    }

    @Test
    void promptTemplateFromExampleProject() {
        String template = "Create a recipe for a {{dishType}} with the following ingredients: {{ingredients}}";
        PromptTemplate promptTemplate = PromptTemplate.from(template);

        Map<String, Object> variables = Map.ofEntries(
                Map.entry("dishType", "oven dish"),
                Map.entry("ingredients", "potato, tomato, feta, olive oil")
        );

        Prompt prompt = promptTemplate.apply(variables);

        String response = model.generate(prompt.text());
        System.out.println(response);
        assertThat(response).contains("potato", "tomato", "feta", "olive oil");

        // This is one response I got:
        @SuppressWarnings("unused")
        String previousResponse = """
                Recipe: Baked Potato and Tomato with Feta Cheese
                
                Ingredients:
                - 4 large potatoes
                - 4 medium-sized tomatoes
                - 200g feta cheese, crumbled
                - 4 tablespoons olive oil
                - Salt and pepper, to taste
                
                Instructions:
                1. Preheat your oven to 400°F (200°C).
                2. Wash the potatoes thoroughly and pat them dry. Prick each potato a few times with a fork to allow steam to escape while baking.
                3. Rub each potato with a little olive oil and sprinkle with salt. Place the potatoes on a baking sheet and bake them in the preheated oven for about 45-60 minutes, or until they are tender and easily pierced with a fork.
                4. While the potatoes are baking, prepare the tomatoes. Wash and slice them into thick rounds, approximately ½-inch thick. Set aside.
                5. Once the potatoes are cooked, remove them from the oven and let them cool slightly. Reduce the oven temperature to 350°F (175°C).
                6. Cut each potato in half lengthwise and scoop out some of the flesh, creating a well in the center. Set aside the scooped-out potato flesh for another use.
                7. Place the potato halves back on the baking sheet, cut side up. Sprinkle a pinch of salt and pepper inside each potato well.
                8. Lay the tomato slices on top of the potato halves, arranging them evenly. Drizzle olive oil over the tomatoes, allowing it to drizzle into the potato wells.
                9. Sprinkle the crumbled feta cheese over the tomatoes, dividing it evenly among the potato halves.
                10. Place the baking sheet back in the oven and bake for an additional 15-20 minutes, or until the feta cheese is slightly melted and the tomatoes are tender.
                11. Once done, remove from the oven and let the dish cool for a few minutes before serving.
                12. Serve the Baked Potato and Tomato with Feta Cheese as a side dish or a light main course. Enjoy!
                Note: Feel free to customize this recipe by adding additional herbs or spices such as chopped fresh basil, oregano, or garlic powder, according to your taste preferences.
                """;
    }
}
