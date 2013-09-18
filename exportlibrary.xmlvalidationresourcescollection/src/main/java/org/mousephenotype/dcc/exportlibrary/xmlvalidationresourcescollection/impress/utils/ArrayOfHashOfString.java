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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import org.apache.xerces.dom.ElementNSImpl;
import org.w3c.dom.NodeList;


/**
 *@author a.retha
 * @author julian
 */


public class ArrayOfHashOfString{
    
    ElementNSImpl source = null;
    
    ArrayList<HashMap<String,String>> items = new ArrayList();
    
    /**
     * constructor
     */
    public ArrayOfHashOfString(ElementNSImpl source){
        setSource(source);
    }
    
    public final void setSource(ElementNSImpl source){
        this.source = source;
        importSource(this.source);
    }
    
    private void importSource(ElementNSImpl source){
        NodeList children = (NodeList) source.getChildNodes();
        for (int i = 0; i < children.getLength(); i++){
            HashMap<String,String> map = new HashMap();
            for (int j = 0; j < children.item(i).getChildNodes().getLength(); j++){
                for(int k = 0; k < children.item(i).getChildNodes().item(j).getChildNodes().getLength(); k+=2){
                    map.put(
                        (String)children.item(i).getChildNodes().item(j).getChildNodes().item(k).getTextContent(), //key
                        (String)children.item(i).getChildNodes().item(j).getChildNodes().item(k+1).getTextContent() //value
                    );
                } 
            }
            this.items.add(map);
        }
    }
    
    public ArrayList<HashMap<String,String>> getHashMapArrayList(){
        return this.items;
    }
    
    public int getLength(){
        return items.size();
    }
    
    public boolean isEmpty(){
        return items.isEmpty();
    }
            
    
    @Override
    public String toString() throws java.util.NoSuchElementException{
        StringBuilder sb = new StringBuilder();
        for (HashMap hm : this.items) {
            Set keys = hm.keySet();
            Iterator j = keys.iterator();
            while(j.hasNext()){
                String key = (String)j.next();
                sb.append("Key: "); 
                sb.append(key); 
                sb.append("\tValue: ");
                sb.append(hm.get(key));
                sb.append("\n");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}

