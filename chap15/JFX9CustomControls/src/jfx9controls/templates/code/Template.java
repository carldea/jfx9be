/*
 * Copyright (c) 2014. by Gerrit Grunwald
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jfx9controls.templates.code;

import javafx.scene.control.Control;
import javafx.scene.control.Skin;


/**
 * User: hansolo
 * Date: 13.02.14
 * Time: 11:42
 */
public class Template extends Control {
    
    // Properties
    

    // ******************** Constructors **************************************
    public Template() {
        getStyleClass().add("template");
    }


    // ******************** Methods *******************************************
    
    

    // ******************** Style related *************************************
    @Override protected Skin createDefaultSkin() {
        return new TemplateSkin(this);
    }

    @Override public String getUserAgentStylesheet() {
        return getClass().getResource("template.css").toExternalForm();
    }    
}
