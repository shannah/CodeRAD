/*
 * Copyright 2020 shannah.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ca.weblite.shared.components;

import com.codename1.ui.CommonProgressAnimations;
import com.codename1.ui.Component;
import static com.codename1.ui.ComponentSelector.$;
import com.codename1.ui.Container;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.util.AsyncResource;

/**
 *
 * @author shannah
 */
public class AsyncComponentWrapper<T extends Component> extends Container {
    private AsyncResource<T> asyncContent;
    
    public AsyncComponentWrapper(AsyncResource<T> asyncContent) {
        super(new BorderLayout());
        $(this).setPadding(0);
        this.asyncContent = asyncContent;
        add(BorderLayout.CENTER, new CommonProgressAnimations.CircleProgress());
        this.asyncContent.onResult((res, err) ->{
            getComponentAt(0).remove();
            if (err != null) {
                return;
            }
            add(BorderLayout.CENTER, res);
            revalidateWithAnimationSafety();
        });
        
    }
    
    public <V extends Component> AsyncResource<V> getContent(Class<V> type) {
        return (AsyncResource<V>)asyncContent;
    }
    
    public AsyncResource<T> getContent() {
        return asyncContent;
    }
}
