package ca.weblite.shared.components;

import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Label;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.Layout;
import com.codename1.ui.table.TableLayout;

import java.util.ArrayList;
import java.util.List;

public class FormContainer extends Container {

    private List<Section> sections = new ArrayList<>();

    private String headingUIID = "FormHeading";

    public static class Section extends Container {
        private int columns=1;
        private List<Field> fields = new ArrayList<Field>();
        private int currRow=0;
        private int currColumn=0;
        private String headingUIID = "FormHeading";


        public Section() {
            setLayout(createLayoutForColumns(columns));
        }

        private TableLayout tableLayout() {
            return (TableLayout)getLayout();
        }

        public void setColumns(int columns) {
            if (columns != this.columns) {
                this.columns = columns;
                setLayout(createLayoutForColumns(columns));
            }
        }

        private static Layout createLayoutForColumns(int columns) {
            if (columns <= 0) {
                throw new IllegalArgumentException("Columns must be greater than 0");
            }
            if (columns == 1) {
                return BoxLayout.y();
            } else {
                return new TableLayout(1, columns);
            }
        }

        private TableLayout.Constraint createNextConstraint(int colSpan) {
            if (currColumn >= columns) {
                currRow++;
                currColumn = 0;
            }
            TableLayout.Constraint constraint = tableLayout().createConstraint(currRow, currColumn);
            constraint.setHorizontalSpan(colSpan);
            return constraint;

        }

        private TableLayout.Constraint createNextHeadingConstraint() {
            if (currColumn > 0) {
                currRow++;
                currColumn = 0;
            }
            TableLayout.Constraint constraint = tableLayout().createConstraint(currRow, currColumn);
            constraint.setHorizontalSpan(columns);
            return constraint;

        }

        public Section addField(Field field) {
            return addField(field, 1);
        }

        public Section addField(Field field, int colSpan) {
            fields.add(field);
            if (columns == 1) {
                add(field);
            } else {
                add(createNextConstraint(colSpan), field);
            }
            return this;
        }

        public Section addHeading(String label) {
            Label headingLabel = new Label(label, headingUIID);
            if (columns == 1) {
                add(headingLabel);
            } else {
                add(createNextHeadingConstraint(), headingLabel);
            }
            return this;
        }
    }

    public static class Field extends Container {
        private final Label label = new Label();
        private Component value;
        private Label description = new Label();
        private HelpButton helpButton;

        private String labelUIID = "FieldLabel";
        private String descriptionUIID = "DescriptionLabel";


        public Field() {
            super(new BorderLayout());
            add(BorderLayout.NORTH, label);
            add(BorderLayout.SOUTH, description);
        }


        public Field setLabel(String label) {
            this.label.setText(label);
            return this;
        }

        public Field setDescription(String description) {
            this.description.setText(description);
            return this;
        }

        public Field setValue(Component cmp) {
            if (this.value != null && contains(this.value)) {
                removeComponent(this.value);
            }
            this.value = cmp;
            if (this.value != null && !contains(this.value)) {
                addComponent(BorderLayout.CENTER, this.value);
            }
            return this;
        }


    }



    public FormContainer() {
        setLayout(BoxLayout.y());
    }

    public FormContainer addField(Field field) {
        return addField(field, 1);
    }

    public FormContainer addField(Field field, int colSpan) {

        currSection().addField(field, colSpan);
        return this;
    }

    private Section currSection() {
        Section section;
        if (sections.isEmpty()) {
            section = new Section();
            section.headingUIID = headingUIID;
            sections.add(section);
            add(section);
        } else {
            section = sections.get(sections.size()-1);
        }
        return section;
    }

    public FormContainer addHeading(String heading) {
        currSection().addHeading(heading);
        return this;
    }

    public FormContainer addSection(Section section) {
        sections.add(section);
        add(section);
        return this;

    }

    public FormContainer setColumns(int cols) {
        currSection().setColumns(cols);
        return this;
    }




}
