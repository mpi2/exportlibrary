/**
 * Copyright (C) 2013 Julian Atienza Herrero <j.atienza at har.mrc.ac.uk>
 *
 * MEDICAL RESEARCH COUNCIL UK MRC
 *
 * Harwell Mammalian Genetics Unit
 *
 * http://www.har.mrc.ac.uk
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.mousephenotype.dcc.exportlibrary.xmlvalidationresourcescollection.impress.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import org.apache.xerces.dom.ElementNSImpl;
import org.w3c.dom.NodeList;

/**
 * @author a.retha
 * @author julian
 */
public class HashOfString {

    ElementNSImpl source = null;
    HashMap<String, String> items = new HashMap<String, String>();

    /**
     * constructor
     */
    public HashOfString(ElementNSImpl source) {
        setSource(source);
    }

    public final void setSource(ElementNSImpl source) {
        this.source = source;
        importSource(this.source);
    }

    private void importSource(ElementNSImpl source) {
        NodeList children = (NodeList) source.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            for (int j = 0; j < children.item(i).getChildNodes().getLength(); j += 2) {
                items.put(
                        (String) children.item(i).getChildNodes().item(j).getTextContent(), //key
                        (String) children.item(i).getChildNodes().item(j + 1).getTextContent() //value
                        );
            }
        }
    }

    public HashMap<String, String> getHashMap() {
        return this.items;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    @Override
    public String toString() throws java.util.NoSuchElementException {
        StringBuilder sb = new StringBuilder();
        Set set = items.keySet();
        Iterator i = set.iterator();
        while (i.hasNext()) {
            String key = (String) i.next();
            sb.append("Key: ");
            sb.append(key);
            sb.append("\tValue: ");
            sb.append(items.get(key));
            sb.append("\n");
        }
        return sb.toString();
    }
}
