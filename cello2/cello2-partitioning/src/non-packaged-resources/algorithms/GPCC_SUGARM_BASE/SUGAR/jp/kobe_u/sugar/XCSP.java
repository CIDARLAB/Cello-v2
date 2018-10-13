package jp.kobe_u.sugar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * NOT IMPLEMENTED.
 * @author Naoyuki Tamura
 */
public class XCSP {
    public List<Domain> domains = new ArrayList<Domain>();
    public List<Variable> variables = new ArrayList<Variable>();
    public List<Relation> relations = new ArrayList<Relation>();
    public List<Predicate> predicates= new ArrayList<Predicate>();
    public List<Constraint> constraints= new ArrayList<Constraint>();
    /*
    private Map<String,Domain> domainMap = new HashMap<String,Domain>();
    private Map<String,Relation> relationMap = new HashMap<String,Relation>();
    private Map<String,Predicate> predicateMap = new HashMap<String,Predicate>();
    */
    private int countConstraint = 0;
    
    public class Domain {
        String name;
        int nbValues;
        String domain;

        /**
         * @param name
         * @param nbValues
         * @param domain
         */
        public Domain(String name, int nbValues, String domain) {
            this.name = name;
            this.nbValues = nbValues;
            this.domain = domain;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            return result;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Domain other = (Domain) obj;
            if (!getOuterType().equals(other.getOuterType()))
                return false;
            if (name == null) {
                if (other.name != null)
                    return false;
            } else if (!name.equals(other.name))
                return false;
            return true;
        }

        private XCSP getOuterType() {
            return XCSP.this;
        }
        
        @Override
        public String toString() {
            return name;
        }
    }
    
    public class Variable {
        String name;
        String domain;

        /**
         * @param name
         * @param domain
         */
        public Variable(String name, String domain) {
            this.name = name;
            this.domain = domain;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            return result;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Variable other = (Variable) obj;
            if (!getOuterType().equals(other.getOuterType()))
                return false;
            if (name == null) {
                if (other.name != null)
                    return false;
            } else if (!name.equals(other.name))
                return false;
            return true;
        }

        private XCSP getOuterType() {
            return XCSP.this;
        }
        
        @Override
        public String toString() {
            return name;
        }
    }
    
    public class Relation {
        String name;
        int arity;
        int nbTuples;
        String semantics;
        String tuples;

        /**
         * @param name
         * @param arity
         * @param nbTuples
         * @param semantics
         * @param tuples
         */
        public Relation(String name, int arity, int nbTuples, String semantics,
                String tuples) {
            this.name = name;
            this.arity = arity;
            this.nbTuples = nbTuples;
            this.semantics = semantics;
            this.tuples = tuples;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            return result;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Relation other = (Relation) obj;
            if (!getOuterType().equals(other.getOuterType()))
                return false;
            if (name == null) {
                if (other.name != null)
                    return false;
            } else if (!name.equals(other.name))
                return false;
            return true;
        }

        private XCSP getOuterType() {
            return XCSP.this;
        }

        @Override
        public String toString() {
            return name;
        }
    }
    
    public class Predicate {
        String name;
        String parameters;
        String expression;
        
        /**
         * @param name
         * @param parameters
         * @param expression
         */
        public Predicate(String name, String parameters, String expression) {
            this.name = name;
            this.parameters = parameters;
            this.expression = expression;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            return result;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Predicate other = (Predicate) obj;
            if (!getOuterType().equals(other.getOuterType()))
                return false;
            if (name == null) {
                if (other.name != null)
                    return false;
            } else if (!name.equals(other.name))
                return false;
            return true;
        }

        private XCSP getOuterType() {
            return XCSP.this;
        }

        @Override
        public String toString() {
            return name;
        }
    }
    
    public class Constraint {
        String name;
        int arity;
        String scope;
        String reference;
        String parameters;
        
        /**
         * @param name
         * @param arity
         * @param scope
         * @param reference
         * @param parameters
         */
        public Constraint(String name, int arity, String scope,
                String reference, String parameters) {
            if (name == null) {
                name = "C" + countConstraint;
                countConstraint++;
            }
            this.name = name;
            this.arity = arity;
            this.scope = scope;
            this.reference = reference;
            this.parameters = parameters;
        }

    
    }
    
    public void add(Domain domain) throws SugarException {
        if (domains.contains(domain))
            throw new SugarException("Duplicate domain " + domain);
    }
}
