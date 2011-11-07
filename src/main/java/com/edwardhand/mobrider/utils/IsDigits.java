package com.edwardhand.mobrider.utils;

public class IsDigits {
    
    public static boolean check(final String input) 
    {
        if (input == null) 
        {
            return false;
        } else if ("".equals(input)) 
        {
            return false;
        } 
        else 
        {
            for (int i = 0; i < input.length(); ++i) 
            {
                if(i == 0)
                {
                    if(!Character.isDigit(input.charAt(i)) && input.charAt(i) != '-')
                        return false;
                }
                else if (!Character.isDigit(input.charAt(i))) 
                    return false;
            }
        }
         
        return true;
    }

}
