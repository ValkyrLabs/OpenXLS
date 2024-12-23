/*
 * --------- BEGIN COPYRIGHT NOTICE ---------
 * Copyright 2002-2012 Extentech Inc.
 * Copyright 2013 Infoteria America Corp.
 *
 * This file is part of OpenXLS.
 *
 * OpenXLS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * OpenXLS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with OpenXLS.  If not, see
 * <http://www.gnu.org/licenses/>.
 * ---------- END COPYRIGHT NOTICE ----------
 */
package com.valkyrlabs.naming;

import javax.naming.*;
import java.util.Hashtable;

/**
 * A basic JNDI Context which holds a flat lookup of names
 */
public class InitialContextImpl implements javax.naming.Context {

    // provide persistence between instantiations
    public static String CONTEXT_ID = "com.valkyrlabs.naming.InitialContextImpl_instance";
    public static String LOAD_CONTEXT = "com.valkyrlabs.naming.load_context";
    protected Hashtable<Comparable, Object> env;
    NameParser nameParser = new NameParserImpl();
    private boolean closed = false;

    public InitialContextImpl() {
        if (System.getProperties().get(CONTEXT_ID) != null)
            this.env = (Hashtable<Comparable, Object>) System.getProperties().get(CONTEXT_ID);
        else {
            String loadme = System.getProperty(LOAD_CONTEXT);
            env = new Hashtable<Comparable, Object>(); // 20070518 KSC: Moved so gets init even if no LOAD_CONTEXT
            if (loadme != null) {
                if (loadme.equals("true")) {
                    // env = new Hashtable(); KSC: See above
                    // this breaks properties
                    System.getProperties().put(CONTEXT_ID, env);
                }
            }
        }
    }

    // check return... -jm
    public Object addToEnvironment(String propName, Object propVal) throws NamingException {
        if (env.contains(propVal)) {
            throw new NamingException("Object " + propName + " already exists in NamingContext.");
        } else {
            env.put(propName, propVal);
            return propVal;
        }
    }

    // we use string to bind -- is that bad?
    public void bind(Name name, Object obj) throws NamingException {
        String str = name.toString();
        this.bind(str, obj);
    }

    public void bind(String name, Object obj) throws NamingException {
        try {
            this.addToEnvironment(name, obj);
        } catch (NamingException e) {
            env.remove(obj);
            env.put(name, obj); // override
        }
    }

    public void close() throws NamingException {
        closed = true;
    }

    // ?
    public Name composeName(Name name, Name prefix) throws NamingException {
        NameImpl retval = new NameImpl();
        retval.addAll(prefix);
        retval.addAll(name);
        return retval;
    }

    public String composeName(String name, String prefix) throws NamingException {
        StringBuffer sb = new StringBuffer();
        sb.append(name);
        sb.append(prefix);
        return sb.toString();
    }

    public Hashtable<Comparable, Object> getEnvironment() throws NamingException {
        return env;
    }

    public NameParser getNameParser(String name) throws NamingException {
        this.nameParser.parse(name);
        return this.nameParser;
    }

    public NameParser getNameParser(Name name) throws NamingException {
        return this.nameParser;
    }

    public Object lookup(Name name) throws NamingException {
        return env.get(name);
    }

    public Object lookup(String name) throws NamingException {
        return env.get(name);
    }

    public Object lookupLink(Name name) throws NamingException {
        return env.get(name);
    }

    public Object lookupLink(String name) throws NamingException {
        return env.get(name);
    }

    public void rebind(Name name, Object obj) throws NamingException {
        this.bind(name, obj);
    }

    public void rebind(String name, Object obj) throws NamingException {
        this.bind(name, obj);
    }

    public Object removeFromEnvironment(String propName) throws NamingException {
        return env.remove(propName);
    }

    public void rename(String oldName, String newName) throws NamingException {
        Object ob = env.get(oldName);
        env.remove(oldName);
        env.put(newName, ob);
    }

    public void rename(Name oldName, Name newName) throws NamingException {
        Object ob = env.get(oldName);
        env.remove(oldName);
        env.put(newName, ob);
    }

    public void unbind(String name) throws NamingException {
        try {
            env.remove(env.get(name));
        } catch (Exception e) {
            throw new NamingException(e.toString());
        }
    }

    public void unbind(Name name) throws NamingException {
        try {
            env.remove(env.get(name));
        } catch (Exception e) {
            throw new NamingException(e.toString());
        }
    }

    // TODO: Implement the following mehods -jm 9/27/2004

    public NamingEnumeration list(String name) throws NamingException {
        return null;
    }

    public NamingEnumeration list(Name name) throws NamingException {
        return null;
    }

    public NamingEnumeration listBindings(Name name) throws NamingException {
        return null;
    }

    public NamingEnumeration listBindings(String name) throws NamingException {
        return null;
    }

    public Context createSubcontext(Name name) throws NamingException {
        // This method is derived from interface javax.naming.Context
        // to do: code goes here
        return null;
    }

    public Context createSubcontext(String name) throws NamingException {
        // This method is derived from interface javax.naming.Context
        // to do: code goes here
        return null;
    }

    public void destroySubcontext(String name) throws NamingException {
        // This method is derived from interface javax.naming.Context
        // to do: code goes here
    }

    public void destroySubcontext(Name name) throws NamingException {
        // This method is derived from interface javax.naming.Context
        // to do: code goes here
    }

    public String getNameInNamespace() throws NamingException {
        // This method is derived from interface javax.naming.Context
        // to do: code goes here
        return null;
    }
}