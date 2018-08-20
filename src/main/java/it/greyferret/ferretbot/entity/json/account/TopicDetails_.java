
package it.greyferret.ferretbot.entity.json.account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TopicDetails_ {

    @SerializedName("topicIds")
    @Expose
    private List<String> topicIds = null;
    @SerializedName("topicCategories")
    @Expose
    private List<String> topicCategories = null;

    public List<String> getTopicIds() {
        return topicIds;
    }

    public void setTopicIds(List<String> topicIds) {
        this.topicIds = topicIds;
    }

    public List<String> getTopicCategories() {
        return topicCategories;
    }

    public void setTopicCategories(List<String> topicCategories) {
        this.topicCategories = topicCategories;
    }

}
