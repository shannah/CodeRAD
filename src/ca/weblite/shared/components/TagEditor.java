/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.shared.components;

import com.codename1.components.CheckBoxList;
import com.codename1.components.InteractionDialog;
import com.codename1.ui.Button;
import static com.codename1.ui.CN.callSerially;
import com.codename1.ui.Container;
import com.codename1.ui.TextField;
import com.codename1.ui.list.MultipleSelectionListModel;

/**
 *
 * @author shannah
 */
public class TagEditor<T> extends Container {
    
    
    public interface OptionFactory<T> {
        public T createOptionWithLabel(String label);
    }
    
    private class TagButton extends Container {
        private int index;
        private Button tagButton;
        private Button deleteButton;
        
        public TagButton(int index) {
            tagButton = new Button(String.valueOf(options.getItemAt(index)), "TagButtonEdit");
            deleteButton = new Button("", "TagButtonDelete");
            
            this.index = index;
            tagButton.addActionListener(e->{
                new TagButtonEditField(this).startEditingAsync();
            });
            deleteButton.addActionListener(e->{
                options.removeSelectedIndices(index);
            });
        }
        
    }
    
    
    private int indexOfOption(String label) {
        int len = options.getSize();
        for (int i=0; i<len; i++) {
            Object o = options.getItemAt(i);
            if (label.equals(String.valueOf(o))) {
                return i;
            }
        }
        return -1;
    }
    
    private class TagButtonEditField extends TextField {
        private TagButton btn;
        private T tag;
        private InteractionDialog optionsDialog;
        public TagButtonEditField(TagButton btn) {
            this.btn = btn;
            
            addActionListener(e->{
                callSerially(()->{
                    Container parent = getParent();
                    if (parent != null) {
                        parent.replace(this, btn, null);
                    } 
                    int index = indexOfOption(getText().trim());
                    if (index < 0) {
                        options.addItem(optionFactory.createOptionWithLabel(getText().trim()));
                        index = indexOfOption(getText().trim());
                    }
                    options.addSelectedIndices(index);
                });
            });
            

            
        }

        @Override
        public void startEditingAsync() {
            if (getParent() != null) {
                super.startEditingAsync();
                return;
            }
            Container parent = btn.getParent();
            if (parent == null) {
                TagEditor.this.add(this);
            } else {
                parent.replace(btn, this, null);
            }
            if (optionsDialog != null && optionsDialog.isShowing()) {
                optionsDialog.dispose();
                
            }
            optionsDialog = new InteractionDialog();
            CheckBoxList checkboxes = new CheckBoxList(options);
            
            super.startEditingAsync();
        }
        
        
    }
    
    public TagEditor(MultipleSelectionListModel<T> options, OptionFactory<T> optionFactory) {
        this.options = options;
        this.optionFactory = optionFactory;
        options.addSelectionListener((oldVal, newVal)->{
            callSerially(()->{
                update();
            });
        });
        options.addDataChangedListener((type, index)->{
            callSerially(()->{
                update();
            });
        });
    }
    
    private void update() {
        removeAll();
        for (int idx : options.getSelectedIndices()) {
            add(new TagButton(idx));
        }
        
    }
    
    
    private MultipleSelectionListModel<T> options;
    private OptionFactory<T> optionFactory;
    
}
