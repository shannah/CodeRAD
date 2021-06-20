package com.codename1.rad.ui.builders;

import com.codename1.rad.annotations.Inject;
import com.codename1.rad.annotations.RAD;
import com.codename1.rad.ui.AbstractComponentBuilder;
import com.codename1.rad.ui.ViewContext;
import com.codename1.ui.Display;
import com.codename1.ui.spinner.Picker;

import java.util.Map;

@RAD(tag={"picker", "stringPicker", "datePicker", "dateTimePicker", "timePicker", "calendarPicker", "durationPicker", "durationHoursPicker", "durationMinutesPicker"})
public class PickerBuilder extends AbstractComponentBuilder<Picker> {
    public PickerBuilder(@Inject ViewContext context, String tagName, Map<String, String> attributes) {
        super(context, tagName, attributes);
    }

    @Override
    public Picker build() {
        Picker picker = new Picker();
        String t = getTagName();
        if ("datePicker".equalsIgnoreCase(t)) {
            picker.setType(Display.PICKER_TYPE_DATE);
        } else if ("dateTimePicker".equalsIgnoreCase(t)) {
            picker.setType(Display.PICKER_TYPE_DATE_AND_TIME);
        } else if ("timePicker".equalsIgnoreCase(t)) {
            picker.setType(Display.PICKER_TYPE_TIME);
        } else if ("calendarPicker".equalsIgnoreCase(t)) {
            picker.setType(Display.PICKER_TYPE_CALENDAR);
        } else if ("durationPicker".equalsIgnoreCase(t)) {
            picker.setType(Display.PICKER_TYPE_DURATION);
        } else if ("durationHoursPicker".equalsIgnoreCase(t)) {
            picker.setType(Display.PICKER_TYPE_DURATION_HOURS);
        } else if ("durationMinutesPicker".equalsIgnoreCase(t)) {
            picker.setType(Display.PICKER_TYPE_DURATION_MINUTES);
        }
        return picker;
    }

    @Override
    public Object parseConstraint(String constraint) {
        return null;
    }
}
