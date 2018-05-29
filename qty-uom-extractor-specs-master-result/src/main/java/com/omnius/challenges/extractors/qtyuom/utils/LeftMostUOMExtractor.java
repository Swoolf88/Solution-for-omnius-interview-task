package com.omnius.challenges.extractors.qtyuom.utils;

import com.omnius.challenges.extractors.qtyuom.QtyUomExtractor;
import com.omnius.challenges.extractors.qtyuom.utils.Pair;

/**
 * Implements {@link QtyUomExtractor} identifying as <strong>the most relevant UOM</strong> the leftmost UOM found in the articleDescription.
 * The {@link UOM} array contains the list of valid UOMs. The algorithm search for the leftmost occurence of UOM[i], if there are no occurrences then tries UOM[i+1].
 * 
 * Example
 * <ul>
 * <li>article description: "black steel bar 35 mm 77 stck"</li>
 * <li>QTY: "77" (and NOT "35")</li>
 * <li>UOM: "stck" (and not "mm" since "stck" has an higher priority as UOM )</li>
 * </ul>
 *
 * @author <a href="mailto:damiano@searchink.com">Damiano Giampaoli</a>
 * @since 4 May 2018
 */
public class LeftMostUOMExtractor implements QtyUomExtractor {

    /**
     * Array of valid UOM to match. the elements with lower index in the array has higher priority
     */
    public static String[] UOM = {"stk", "stk.", "stck", "stück", "stg", "stg.", "st", "st.", "stange", "stange(n)", "tafel", "tfl", "taf", "mtr", "meter", "qm", "kg", "lfm", "mm", "m"};
    
    public LeftMostUOMExtractor() {}
    
    public static void main(String [] args)
	{
    	new LeftMostUOMExtractor().extract("black steel bar 35 mm 77 stck");
  	}
    
    @Override
    public Pair<String, String> extract(String articleDescription) {
        //mock implementation
    	if(articleDescription == null || articleDescription.isEmpty() || articleDescription.replaceAll("\\s","").isEmpty()){
    		return null;
    	}
    	String quantity = extractAsStringtoDouble(articleDescription).getFirst();
    	String uomResult = extractAsStringtoDouble(articleDescription).getSecond();
        return new Pair<String, String>(quantity,uomResult);
    }
    
    @Override
    public Pair<Double, String> extractAsDouble(String articleDescription) {
        //mock implementation
    	if(articleDescription == null || articleDescription.isEmpty() || articleDescription.replaceAll("\\s","").isEmpty()){
    		return null;
    	}    	
    	String quantity = extractAsStringtoDouble(articleDescription).getFirst();
    	double quantityvalue = Double.parseDouble(quantity);
    	String uomResult = extractAsStringtoDouble(articleDescription).getSecond();
        return new Pair<Double, String>(quantityvalue,uomResult);
    }

    @Override
    public Pair<String, String> extractAsStringtoDouble(String articleDescription) {
    	articleDescription = articleDescription.toLowerCase();
    	String[] words = articleDescription.split("\\s+");    	
    	int min = Integer.MAX_VALUE;
    	int m=1;
    	String wordQTY = "";
    	for(int i = 0; i < words.length; i++){
    		if(words[i].equals("st�ck")){
    			words[i]="stück";
    		}
    		for(int j = 0; j < UOM.length; j++){    			
    			if(words[i].equals(UOM[j])){   				 
    			        for (int k = 1; k < words.length; k++) {
    			            if (j < min) {
    			                min = j;
    			                wordQTY = "";
    			                if(words[1].equals(UOM[j])) {
        			                wordQTY = words[0];        			                
        			            }
    			                if(words[0].equals(",")) {
        			                wordQTY = words[i-1];        			                
        			            }
    			                if(i > 1){
    			                if(!words[0].equals(",")){
    			                if((words[i-2].equals(QtyUomExtractor.DECIMAL_SEPARATOR[0]) || words[i-2].equals(QtyUomExtractor.DECIMAL_SEPARATOR[1])) && words[i-3].matches("[0-9]+")){
    			                	if(words[i-m-2].matches("[0-9]+") && words[i-m-2].length()>3){
			                			wordQTY = words[i-m-2] + wordQTY;
			                		}
    			                	while(words[i-m-2].matches("[0-9]+") && words[i-m-2].length()<4){ 
    			                		if(words[i-m-3].matches("[0-9]+") && words[i-m-3].length()<4 && words[i-m-2].length()<3){
    			                			wordQTY = words[i-m-2] + wordQTY;
        			                		m++;
        			                	} else {
        			                		wordQTY = words[i-m-2] + wordQTY;
        			                	}
    		                			m++;
        			                	}  	
    			                		wordQTY = wordQTY + words[i-2] + words[i-1];
    			                		m = 1;    			                
    			                } else if(words[i-1].matches("[0-9]+")) {
    			                	if(words[i-m].length()>3){
    			                		wordQTY = words[i-m];
    			                	}
    			                	while(words[i-m].matches("[0-9]+") && words[i-m].length()<4){
    			                		if(words[i-m-1].matches("[0-9]+") && words[i-m-1].length()<4 && words[i-m].length()<3){
    			                			wordQTY = words[i-m] + wordQTY;
    			                			m++;
    			                		} else {
    			                			wordQTY = words[i-m] + wordQTY;
    			                		}
    			                		m++;    			                		
    			                		}
    			                		m = 1;
    			                 } else if((words[i-1].contains(QtyUomExtractor.DECIMAL_SEPARATOR[0]) || words[i-1].contains(QtyUomExtractor.DECIMAL_SEPARATOR[1]))) {    			                	    			                	
    			                	 if(i-m-2>0){
    			                	 while(words[i-m-1].matches("[0-9]+") && words[i-m-1].length()<4){
    			                		 
    			                		if(words[i-m-2].matches("[0-9]+") && words[i-m-2].length()<4 && words[i-m-1].length()<3){
    			                			wordQTY = words[i-m-1] + wordQTY;
    			                			m++;    			                		
    			                		} else {
    			                			wordQTY = words[i-m-1] + wordQTY;	
    			                		}
    			                		m++;
    			                		}
    			                	 }
    			                	 wordQTY = wordQTY + words[i-1];    			                			
    			                } else {
    			                	wordQTY = words[i-1];    			                	
    			                }
    			               }
    			             }
    			           }
    			        }    			        
    				}
    			}
    		}
    	//System.out.println("result: QTY: " + wordQTY+ " UOM: " +UOM[min]); //print the result
        return new Pair<String, String>(wordQTY,UOM[min]);
    }
}
