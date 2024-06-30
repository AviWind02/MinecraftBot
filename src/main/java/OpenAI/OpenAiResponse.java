package OpenAI;
import java.util.List;

public class OpenAiResponse {
    private List<OpenAiChoice> choices;

    public List<OpenAiChoice> getChoices() {
        return choices;
    }

    public void setChoices(List<OpenAiChoice> choices) {
        this.choices = choices;
    }
}