package org.pvemu.commands;

import org.pvemu.commands.argument.ArgumentList;
import org.pvemu.commands.argument.CommandArgumentException;
import org.pvemu.commands.askers.Asker;
import org.pvemu.jelly.filters.Filter;
import org.pvemu.jelly.filters.FilterFactory;

/**
 *
 * @author Vincent Quatrevieux <quatrevieux.vincent@gmail.com>
 */
abstract public class Command {
    /**
     * Get the command name, use to identify him.
     * @return the command name
     */
    abstract public String name();
    
    abstract public void perform(ArgumentList args, Asker asker) throws CommandArgumentException;
    
    /**
     * Get the title of the command (show in the help commad)
     * @return 
     * @see HelpCommand
     */
    public String title(){
        return name();
    }
    
    /**
     * Get the usage of the command
     * @return an array of string witch represents the lines to sends
     * @see HelpCommand
     */
    public String[] usage(){
        return new String[]{
            "Commande non documenté."
        };
    }
    
    /**
     * The conditions for execute command
     * @return the filter witch represents those conditions
     */
    public Filter conditions(){
        return FilterFactory.yesFilter();
    }
}
