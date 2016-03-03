package bd.com.ipay.ipayskeleton.Model.MMModule.Events;

import java.util.List;

import bd.com.ipay.ipayskeleton.Model.MMModule.Events.EventClasses.EventCategory;

public class GetEventCategoriesResponse {

    private List<EventCategory> categories;

    public GetEventCategoriesResponse() {
    }

    public List<EventCategory> getCategories() {
        return categories;
    }
}
