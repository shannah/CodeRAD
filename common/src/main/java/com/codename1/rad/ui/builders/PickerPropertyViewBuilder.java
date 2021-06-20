package com.codename1.rad.ui.builders;

import com.codename1.rad.annotations.Inject;
import com.codename1.rad.annotations.RAD;
import com.codename1.rad.propertyviews.PickerPropertyView;

import com.codename1.rad.ui.ViewContext;
import com.codename1.ui.Display;
import com.codename1.ui.spinner.Picker;

import java.util.Map;

@RAD(tag={"pickerPropertyView", "radPicker", "radDatePicker", "radTimePicker", "radDateTimePicker", "radCalendarPicker", "radStringPicker", "radDurationPicker", "radDurationHoursPicker", "radDurationMinutesPicker"})
public class PickerPropertyViewBuilder extends PropertyViewBuilder<Picker> {
    private Picker picker;
    public PickerPropertyViewBuilder(ViewContext context, String tagName, Map<String, String> attributes) {
        super(context, tagName, attributes);
    }

    public PickerPropertyViewBuilder picker(@Inject Picker picker) {
        this.picker = picker;
        return this;
    }

    @Override
    public PickerPropertyView build() {
        if (fieldNode == null) {
            throw new IllegalStateException("PickerPropertyView requires tag to be set");
        }

        Picker picker = this.picker == null ? new Picker() : this.picker;
        String t = getTagName();
        if ("raddatePicker".equalsIgnoreCase(t)) {
            picker.setType(Display.PICKER_TYPE_DATE);
        } else if ("raddateTimePicker".equalsIgnoreCase(t)) {
            picker.setType(Display.PICKER_TYPE_DATE_AND_TIME);
        } else if ("radtimePicker".equalsIgnoreCase(t)) {
            picker.setType(Display.PICKER_TYPE_TIME);
        } else if ("radcalendarPicker".equalsIgnoreCase(t)) {
            picker.setType(Display.PICKER_TYPE_CALENDAR);
        } else if ("raddurationPicker".equalsIgnoreCase(t)) {
            picker.setType(Display.PICKER_TYPE_DURATION);
        } else if ("raddurationHoursPicker".equalsIgnoreCase(t)) {
            picker.setType(Display.PICKER_TYPE_DURATION_HOURS);
        } else if ("raddurationMinutesPicker".equalsIgnoreCase(t)) {
            picker.setType(Display.PICKER_TYPE_DURATION_MINUTES);
        }
        return new PickerPropertyView(picker, getEntity(), fieldNode);
    }

    @Override
    public PickerPropertyView getComponent() {
        return (PickerPropertyView) super.getComponent();
    }
}
