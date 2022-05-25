import java.util.HashMap;
import java.util.Stack;
import java.util.Vector;

public class Parser {

    public enum State {
        START, TERM, SET_OPEN, OPEN_PAREN, EXPR_QUANT, BACKSLASH, QUANT_STAR, QUANT_OPT, INTERVAL_OPEN, OPEN_PAREN_QUANT, EXPR_CONT, OPEN_PAREN_QUANT2,
        PAREN_COMMENT, OPEN_PAREN_EXTENDED, OPEN_PAREN_LOOKBEHIND, PAREN_FLAG,NAMED_CAPTURE,QUANT_PLUS,INTERVAL_LOWER,INTERVAL_UPPER,INTERVAL_TYPE,NAMED_BACKREF,NAMED_BACKREF2,NAMED_BACKREF3,SET_AFTER_LIT,
        SET_AFTER_SET,SET_AFTER_RANGE, SET_POSIX, SET_OPEN2, SET_START, SET_ESCAPE, SET_START_DASH, SET_START_AMP, SET_LIT_AMP, SET_SET_DASH, SET_SET_AMP, SET_RANGE_DASH, SET_RANGE_AMP, SET_AFTER_OP,SET_LIT_DASH,
        SET_LIT_DASH_ESCAPE,SET_FINISH;
    }

    public class StateElement
    {
        public State state;
        public int nPos;

        public StateElement(final State state, final int nPos)
        {
            this.state = state;
            this.nPos = nPos;
        }

        public StateElement()
        {}
    }



    public Expression Parse(final String str) {

        Vector<StateElement> states = new Vector<>();
        final Stack<State> stack = new Stack<>();

        states.add(new StateElement(State.START,0));
        int k = 0;
        while (k < str.length()) {
            switch (states.lastElement().state) {
                case START: {
                    states.add(new StateElement(State.TERM,k));
                }
                case TERM: {
                    switch (str.charAt(k)) {
                        case '[': {
                            states.add(new StateElement(State.SET_OPEN,k));
                            k++;
                            stack.add(State.SET_FINISH);
                            break;
                        }

                        case '(': {
                            states.add(new StateElement(State.OPEN_PAREN,k));
                            k++;
                            break;
                        }

                        case '.':

                        case '$':

                        case '^': {
                            states.add(new StateElement(State.EXPR_QUANT,k));
                            k++;
                            break;
                        }

                        case '\\': {
                            states.add(new StateElement(State.BACKSLASH,k));
                            k++;
                            break;
                        }

                        case '|': {
                            states.add(new StateElement(State.TERM,k));
                            k++;
                            break;
                        }

                        case ')': {
                            if(stack.size() == 0)
                                return new Expression(str, false, k, "EROARE: EXPRESIE INCORECTA LA POZITIA " + k + " NU EXISTA PARANTEZA DESCHISA");
                            states.add(new StateElement(stack.pop(),k));
                            k++;
                            break;
                        }

                        default: {
                            if (str.charAt(k) >= 'a' && str.charAt(k) <= 'z' || str.charAt(k) >= 'A' && str.charAt(k) <= 'Z')
                            {
                                states.add(new StateElement(State.EXPR_QUANT,k));
                                k++;
                            }
                            else
                                return new Expression(str, false, k, "EROARE: EXPRESIE INCORECTA LA POZITIA " + k);
                        }

                    }
                    break;
                }
                case EXPR_QUANT: {
                    switch (str.charAt(k)) {
                        case '*':
                        {
                            states.add(new StateElement(State.QUANT_STAR,k));
                            k++;
                            break;
                        }
                        case '+': {
                            states.add(new StateElement(State.QUANT_PLUS,k));
                            k++;
                            break;
                        }
                        case '?': {
                            states.add(new StateElement(State.QUANT_OPT,k));
                            k++;
                            break;
                        }
                        case '{': {
                            states.add(new StateElement(State.INTERVAL_OPEN,k));
                            k++;
                            break;
                        }
                        case '(': {
                            states.add(new StateElement(State.OPEN_PAREN_QUANT,k));
                            k++;
                            break;
                        }
                        default: {
                            states.add(new StateElement(State.EXPR_CONT,k));
                            break;
                        }
                    }
                    break;
                }

                case EXPR_CONT: {
                    switch (str.charAt(k)) {
                        case '|': {
                            states.add(new StateElement(State.TERM,k));
                            k++;
                            break;
                        }
                        case ')': {
                            if(stack.size() == 0)
                                return new Expression(str, false, k, "EROARE: EXPRESIE INCORECTA LA POZITIA " + k + " NU EXISTA PARANTEZA DESCHISA");
                            states.add(new StateElement(stack.pop(),k));
                            k++;
                            break;
                        }
                        default: {
                            states.add(new StateElement(State.TERM,k));
                            break;
                        }
                    }
                    break;
                }

                case OPEN_PAREN_QUANT: {
                    if (str.charAt(k) == '?') {
                        states.add(new StateElement(State.OPEN_PAREN_QUANT2,k));
                        k++;
                    } else {
                        states.add(new StateElement(State.OPEN_PAREN,k));
                    }
                    break;
                }

                case OPEN_PAREN_QUANT2: {
                    if (str.charAt(k) == '#') {
                        states.add(new StateElement(State.PAREN_COMMENT,k));
                        k++;
                        stack.add(State.EXPR_QUANT);
                    } else {
                        states.add(new StateElement(State.OPEN_PAREN_EXTENDED,k));
                    }
                    break;
                }

                case OPEN_PAREN: {
                    if (str.charAt(k) == '?') {
                        states.add(new StateElement(State.OPEN_PAREN_EXTENDED,k));
                        k++;
                    } else {
                        states.add(new StateElement(State.TERM,k));
                        stack.add(State.EXPR_QUANT);
                    }
                    break;
                }

                case OPEN_PAREN_EXTENDED: {
                    switch (str.charAt(k)) {
                        case ':':
                        case '>':
                        {
                            states.add(new StateElement(State.TERM,k));
                            stack.add(State.EXPR_QUANT);
                            k++;
                            break;
                        }
                        case '=':
                        case '!': {
                            states.add(new StateElement(State.TERM,k));
                            stack.add(State.EXPR_CONT);
                            k++;
                            break;
                        }
                        case '<': {
                            states.add(new StateElement(State.OPEN_PAREN_LOOKBEHIND,k));
                            k++;
                            break;
                        }
                        case '#': {
                            states.add(new StateElement(State.PAREN_COMMENT,k));
                            stack.add(State.TERM);
                            k++;
                            break;
                        }
                        case 'i':
                        case 'd':
                        case 'm':
                        case 's':
                        case 'u':
                        case 'w':
                        case 'x':
                        case '-': {
                            states.add(new StateElement(State.PAREN_FLAG,k));
                            break;
                        }
                        default: {
                            return new Expression(str, false, k, "EROARE: EXPRESIE INCORECTA LA POZITIA " + k);
                        }
                    }
                    break;
                }

                case OPEN_PAREN_LOOKBEHIND:
                {
                    switch(str.charAt(k))
                    {
                        case '=':
                        case '!': {
                            states.add(new StateElement(State.TERM,k));
                            stack.add(State.EXPR_CONT);
                            k++;
                            break;
                        }
                        default:
                        {
                            if((str.charAt(k) >= 'a' && str.charAt(k) <= 'z') || ((str.charAt(k) >= 'A' && str.charAt(k) <= 'Z')))
                            states.add(new StateElement(State.NAMED_CAPTURE,k));
			                else
                                return new Expression(str, false, k, "EROARE: EXPRESIE INCORECTA LA POZITIA " + k);
                            break;
                        }
                    }
                    break;
                }

                case PAREN_COMMENT:
                {
                    if (str.charAt(k) == ')') {
                        if(stack.size() == 0)
                            return new Expression(str, false, k, "EROARE: EXPRESIE INCORECTA LA POZITIA " + k + " NU EXISTA PARANTEZA DESCHISA");
                        states.add(new StateElement(stack.pop(),k));
                        k++;
                    } else {
                        states.add(new StateElement(State.PAREN_COMMENT,k));
                        k++;
                    }
                    break;
                }

                case PAREN_FLAG:
                {
                    switch(str.charAt(k))
                    {
                        case 'i':
                        case 'd':
                        case 'm':
                        case 's':
                        case 'u':
                        case 'w':
                        case 'x':
                        case '-': {
                            states.add(new StateElement(State.PAREN_FLAG,k));
                            k++;
                            break;
                        }
                        case ')':
                        {
                            states.add(new StateElement(State.TERM,k));
                            k++;
                            break;
                        }
                        case ':': {
                            states.add(new StateElement(State.TERM,k));
                            stack.add(State.EXPR_QUANT);
                            k++;
                            break;
                        }
                        default:
                        {
                            return new Expression(str, false, k, "EROARE: EXPRESIE INCORECTA LA POZITIA " + k);
                        }
                    }
                    break;
                }

                case NAMED_CAPTURE:
                {
                    if (str.charAt(k) == '>') {
                        states.add(new StateElement(State.TERM,k));
                        stack.add(State.EXPR_QUANT);
                        k++;
                    } else {
                        if ((str.charAt(k) >= 'a' && str.charAt(k) <= 'z') || (str.charAt(k) >= 'A' && str.charAt(k) <= 'Z') || (str.charAt(k) >= '0' && str.charAt(k) <= '9')) {
                            states.add(new StateElement(State.NAMED_CAPTURE,k));
                            k++;
                        } else
                            return new Expression(str, false, k, "EROARE: EXPRESIE INCORECTA LA POZITIA " + k);
                    }
                    break;
                }

                case QUANT_STAR:

                case QUANT_PLUS:

                case QUANT_OPT: {
                    switch(str.charAt(k))
                    {
                        case '?':
                        case '+': {
                            states.add(new StateElement(State.EXPR_CONT,k));
                            k++;
                            break;
                        }
                        default:
                        {
                            states.add(new StateElement(State.EXPR_CONT,k));
                            break;
                        }
                    }
                    break;
                }

                case INTERVAL_OPEN:
                {
                    if(str.charAt(k) >= '0' && str.charAt(k) <= '9')
                        states.add(new StateElement(State.INTERVAL_LOWER,k));
                    else
                        return new Expression(str, false, k, "EROARE: EXPRESIE INCORECTA LA POZITIA " + k);
                    break;
                }

                case INTERVAL_LOWER:
                {
                    switch(str.charAt(k))
                    {
                        case ',':
                        {
                            states.add(new StateElement(State.INTERVAL_UPPER,k));
                            k++;
                            break;
                        }
                        case '}':
                        {
                            states.add(new StateElement(State.INTERVAL_TYPE,k));
                            k++;
                            break;
                        }
                        default:
                        {
                            if(str.charAt(k) >= '0' && str.charAt(k) <= '9')
                            {
                                states.add(new StateElement(State.INTERVAL_LOWER,k));
                                k++;
                            }
                            else
                                return new Expression(str, false, k, "EROARE: EXPRESIE INCORECTA LA POZITIA " + k);
                            break;
                        }
                    }
                    break;
                }

                case INTERVAL_UPPER:
                {
                    if (str.charAt(k) == '}') {
                        states.add(new StateElement(State.INTERVAL_TYPE,k));
                        k++;
                    } else {
                        if (str.charAt(k) >= '0' && str.charAt(k) <= '9') {
                            states.add(new StateElement(State.INTERVAL_UPPER,k));
                            k++;
                        } else
                            return new Expression(str, false, k, "EROARE: EXPRESIE INCORECTA LA POZITIA " + k);
                    }
                    break;
                }

                case INTERVAL_TYPE:
                {
                    switch(str.charAt(k))
                    {
                        case '?':
                        case '+': {
                            states.add(new StateElement(State.EXPR_CONT,k));
                            k++;
                            break;
                        }
                        default:
                        {
                            states.add(new StateElement(State.EXPR_CONT,k));
                        }
                    }
                    break;
                }

                case BACKSLASH:
                {
                    switch(str.charAt(k))
                    {
                        case 'A':
                        case 'B':
                        case 'b':
                        case 'G':
                        case 'Z':
                        case 'z':
                        case 'Q': {
                            states.add(new StateElement(State.TERM,k));
                            k++;
                            break;
                        }
                        case 'd':
                        {
                            states.add(new StateElement(State.EXPR_QUANT,k));
                            k++;
                            break;
                        }
                        case 'D':
                        {
                            states.add(new StateElement(State.EXPR_QUANT,k));
                            k++;
                            break;
                        }
                        case 'h':
                        {
                            states.add(new StateElement(State.EXPR_QUANT,k));
                            k++;
                            break;
                        }
                        case 'H':
                        {
                            states.add(new StateElement(State.EXPR_QUANT,k));
                            k++;
                            break;
                        }
                        case 'k':
                        {
                            states.add(new StateElement(State.NAMED_BACKREF,k));
                            k++;
                            break;
                        }
                        case 'N':
                        case 'p':
                        case 'P': {
                            states.add(new StateElement(State.EXPR_QUANT,k));
                            break;
                        }
                        case 'R':
                        {
                            states.add(new StateElement(State.EXPR_QUANT,k));
                            k++;
                            break;
                        }
                        case 'S':
                        {
                            states.add(new StateElement(State.EXPR_QUANT,k));
                            k++;
                            break;
                        }
                        case 's':
                        {
                            states.add(new StateElement(State.EXPR_QUANT,k));
                            k++;
                            break;
                        }
                        case 'v':
                        {
                            states.add(new StateElement(State.EXPR_QUANT,k));
                            k++;
                            break;
                        }
                        case 'V':
                        {
                            states.add(new StateElement(State.EXPR_QUANT,k));
                            k++;
                            break;
                        }
                        case 'W':
                        {
                            states.add(new StateElement(State.EXPR_QUANT,k));
                            k++;
                            break;
                        }
                        case 'w':
                        {
                            states.add(new StateElement(State.EXPR_QUANT,k));
                            k++;
                            break;
                        }
                        case 'X':
                        {
                            states.add(new StateElement(State.EXPR_QUANT,k));
                            k++;
                            break;
                        }
                        default:
                        {
                            states.add(new StateElement(State.EXPR_QUANT,k));
                            k++;
                            break;
                        }
                    }
                    break;
                }

                case NAMED_BACKREF:
                {
                    if (str.charAt(k) == '<') {
                        states.add(new StateElement(State.NAMED_BACKREF2,k));
                        k++;
                    } else {
                        return new Expression(str, false, k, "EROARE: EXPRESIE INCORECTA LA POZITIA " + k);
                    }
                    break;
                }

                case NAMED_BACKREF2:
                {
                    if((str.charAt(k) >= 'a' && str.charAt(k) <= 'z') || (str.charAt(k) >= 'A' && str.charAt(k) <= 'Z')) {
                        states.add(new StateElement(State.NAMED_BACKREF3,k));
                        k++;
                    }
                    else
                        return new Expression(str, false, k, "EROARE: EXPRESIE INCORECTA LA POZITIA " + k);
                    break;
                }

                case NAMED_BACKREF3:
                {
                    if((str.charAt(k) >= 'a' && str.charAt(k) <= 'z') || (str.charAt(k) >= 'A' && str.charAt(k) <= 'Z') || (str.charAt(k) >= '0' && str.charAt(k) <= '9')) {
                        states.add(new StateElement(State.NAMED_BACKREF3,k));
                        k++;
                    }
                    else if(str.charAt(k) == '>') {
                        states.add(new StateElement(State.EXPR_QUANT,k));
                        k++;
                    }
                    else
                        return new Expression(str, false, k, "EROARE: EXPRESIE INCORECTA LA POZITIA " + k);
                    break;
                }

                case SET_OPEN:
                {
                    switch(str.charAt(k))
                    {
                        case '^': {
                            states.add(new StateElement(State.SET_OPEN2,k));
                            k++;
                            break;
                        }
                        case ':': {
                            states.add(new StateElement(State.SET_POSIX,k));
                            break;
                        }
                        default:
                        {
                            states.add(new StateElement(State.SET_OPEN2,k));
                            break;
                        }
                    }
                    break;
                }

                case SET_OPEN2:
                {
                    if (str.charAt(k) == ']') {
                        states.add(new StateElement(State.SET_AFTER_LIT,k));
                        k++;
                    } else {
                        states.add(new StateElement(State.SET_START,k));
                    }
                    break;
                }

                case SET_POSIX:
                {
                    switch(str.charAt(k))
                    {
                        case ']': {
                            if(stack.size() == 0)
                                return new Expression(str, false, k, "EROARE: EXPRESIE INCORECTA LA POZITIA " + k + "NU EXISTA PARANTEZA DREAPTA DESCHISA");
                            states.add(new StateElement(stack.pop(),k));
                            k++;
                            break;
                        }
                        case ':': {
                            states.add(new StateElement(State.SET_START,k));
                            break;
                        }
                        default:
                        {
                            return new Expression(str, false, k, "EROARE: EXPRESIE INCORECTA LA POZITIA " + k);
                        }
                    }
                    break;
                }

                case SET_START:
                {
                    switch(str.charAt(k))
                    {
                        case ']': {
                            if(stack.size() == 0)
                                return new Expression(str, false, k, "EROARE: EXPRESIE INCORECTA LA POZITIA " + k + "NU EXISTA PARANTEZA DREAPTA DESCHISA");
                            states.add(new StateElement(stack.pop(),k));
                            k++;
                            break;
                        }
                        case '[': {
                            states.add(new StateElement(State.SET_OPEN,k));
                            stack.add(State.SET_AFTER_SET);
                            k++;
                            break;
                        }
                        case '\\': {
                            states.add(new StateElement(State.SET_ESCAPE,k));
                            k++;
                            break;
                        }
                        case '-': {
                            states.add(new StateElement(State.SET_START_DASH,k));
                            k++;
                            break;
                        }
                        case '&': {
                            states.add(new StateElement(State.SET_START_AMP,k));
                            k++;
                            break;
                        }
                        default:
                        {
                            states.add(new StateElement(State.SET_AFTER_LIT,k));
                            k++;
                            break; 
                       }
                    }
                    break;
                }

                case SET_START_DASH:
                {
                    if (str.charAt(k) == '-') {
                        return new Expression(str, false, k, "EROARE: EXPRESIE INCORECTA LA POZITIA " + k +" NU SE POATE PUNE --");
                    } else {
                        states.add(new StateElement(State.SET_AFTER_LIT,k));
                    }
                    break;
                }

                case SET_START_AMP:
                {
                    if (str.charAt(k) == '&') {
                        return new Expression(str, false, k, "EROARE: EXPRESIE INCORECTA LA POZITIA " + k + " NU SE POATE PUNE &&");
                    } else {
                        states.add(new StateElement(State.SET_AFTER_LIT,k));
                    }
                    break;
                }

                case SET_AFTER_LIT:
                {
                    switch(str.charAt(k))
                    {
                        case ']': {
                            if(stack.size() == 0)
                                return new Expression(str, false, k, "EROARE: EXPRESIE INCORECTA LA POZITIA " + k + "NU EXISTA PARANTEZA DREAPTA DESCHISA");
                            states.add(new StateElement(stack.pop(),k));
                            k++;
                            break; 
                        }
                        case '[': {
                            states.add(new StateElement(State.SET_OPEN,k));
                            stack.add(State.SET_AFTER_SET);
                            k++;
                            break; 
                        }
                        case '-': {
                            states.add(new StateElement(State.SET_LIT_DASH,k));
                            k++;
                            break; 
                        }
                        case '&': {
                            states.add(new StateElement(State.SET_LIT_AMP,k));
                            k++;
                            break; 
                        }
                        case '\\': {
                            states.add(new StateElement(State.SET_ESCAPE,k));
                            k++;
                            break; 
                        }
                        default:
                        {
                            states.add(new StateElement(State.SET_AFTER_LIT,k));
                            k++;
                            break;                        
                        }
                    }
                    break;
                }

                case SET_AFTER_SET:
                {
                    switch(str.charAt(k))
                    {
                        case ']': {
                            if(stack.size() == 0)
                                return new Expression(str, false, k, "EROARE: EXPRESIE INCORECTA LA POZITIA " + k + "NU EXISTA PARANTEZA DREAPTA DESCHISA");
                            states.add(new StateElement(stack.pop(),k));
                            k++;
                            break; 
                        }
                        case '[': {
                            states.add(new StateElement(State.SET_OPEN,k));
                            stack.add(State.SET_AFTER_SET);
                            k++;
                            break; 
                        }
                        case '-': {
                            states.add(new StateElement(State.SET_SET_DASH,k));
                            k++;
                            break; 
                        }
                        case '&': {
                            states.add(new StateElement(State.SET_SET_AMP,k));
                            k++;
                            break; 
                        }
                        case '\\': {
                            states.add(new StateElement(State.SET_ESCAPE,k));
                            k++;
                            break; 
                        }
                        default:
                        {
                            states.add(new StateElement(State.SET_AFTER_LIT,k));
                            k++;
                            break;                        
                        }
                    }
                    break;
                }

                case SET_AFTER_RANGE:
                {
                    switch(str.charAt(k))
                    {
                        case ']': {
                            if(stack.size() == 0)
                                return new Expression(str, false, k, "EROARE: EXPRESIE INCORECTA LA POZITIA " + k + "NU EXISTA PARANTEZA DREAPTA DESCHISA");
                            states.add(new StateElement(stack.pop(),k));
                            k++;
                            break; 
                        }
                        case '[': {
                            states.add(new StateElement(State.SET_OPEN,k));
                            stack.add(State.SET_AFTER_SET);
                            k++;
                            break; 
                        }
                        case '-': {
                            states.add(new StateElement(State.SET_RANGE_DASH,k));
                            k++;
                            break; 
                        }
                        case '&': {
                            states.add(new StateElement(State.SET_RANGE_AMP,k));
                            k++;
                            break; 
                        }
                        case '\\': {
                            states.add(new StateElement(State.SET_ESCAPE,k));
                            k++;
                            break; 
                        }
                        default:
                        {
                            states.add(new StateElement(State.SET_AFTER_LIT,k));
                            k++;
                            break;                        
                        }
                    }
                    break;
                }

                case SET_AFTER_OP:
                {
                    switch(str.charAt(k))
                    {
                        case ']': {
                            return new Expression(str, false, k, "EROARE: EXPRESIE INCORECTA LA POZITIA " + k);
                        }
                        case '[': {
                            states.add(new StateElement(State.SET_OPEN,k));
                            stack.add(State.SET_AFTER_SET);
                            k++;
                            break; 
                        }
                        case '\\': {
                            states.add(new StateElement(State.SET_ESCAPE,k));
                            k++;
                            break; 
                        }
                        default:
                        {
                            states.add(new StateElement(State.SET_AFTER_LIT,k));
                            k++;
                            break;                        
                        }
                    }
                    break;
                }

                case SET_SET_AMP:
                {
                    switch(str.charAt(k))
                    {
                        case '[': {
                            states.add(new StateElement(State.SET_OPEN,k));
                            stack.add(State.SET_AFTER_SET);
                            k++;
                            break; 
                        }
                        case '&': {
                            states.add(new StateElement(State.SET_AFTER_OP,k));
                            k++;
                            break; 
                        }
                        default:
                        {
                            states.add(new StateElement(State.SET_AFTER_LIT,k));
                            break;                        
                        }
                    }
                    break;
                }
                case SET_RANGE_AMP:
                case SET_LIT_AMP:
                {
                    if (str.charAt(k) == '&') {
                        states.add(new StateElement(State.SET_AFTER_OP,k));
                        k++;
                    } else {
                        states.add(new StateElement(State.SET_AFTER_LIT,k));
                    }
                    break;
                }

                case SET_SET_DASH:
                {
                    switch (str.charAt(k))
                    {
                        case '[':
                        {
                            states.add(new StateElement(State.SET_OPEN,k));
                            stack.add(State.SET_AFTER_SET);
                            k++;
                            break;
                        }
                        case '-':
                        {
                            states.add(new StateElement(State.SET_AFTER_OP,k));
                            k++;
                            break;
                        }
                        default:
                        {
                            states.add(new StateElement(State.SET_AFTER_LIT,k));
                        }
                    }
                    break;
                }

                case SET_RANGE_DASH:
                {
                    if (str.charAt(k) == '-') {
                        states.add(new StateElement(State.SET_AFTER_OP,k));
                        k++;
                    } else {
                        states.add(new StateElement(State.SET_AFTER_LIT,k));
                    }
                    break;
                }

                case SET_LIT_DASH:
                {
                    switch (str.charAt(k))
                    {
                        case '-':
                        {
                            states.add(new StateElement(State.SET_AFTER_OP,k));
                            k++;
                            break;
                        }
                        case '[':
                        case ']':
                        {
                            states.add(new StateElement(State.SET_AFTER_LIT,k));
                            break;
                        }
                        case '\\':
                        {
                            states.add(new StateElement(State.SET_LIT_DASH_ESCAPE,k));
                            k++;
                            break;
                        }
                        default:
                        {
                            states.add(new StateElement(State.SET_AFTER_RANGE,k));
                            k++;
                        }
                    }
                    break;
                }

                case SET_LIT_DASH_ESCAPE:
                {
                    switch (str.charAt(k))
                    {
                        case 's':
                        case 'S':
                        case 'w':
                        case 'W':
                        case 'd':
                        case 'D':
                        {
                            return new Expression(str, false, k, "EROARE: EXPRESIE INCORECTA LA POZITIA " + k);
                        }
                        case 'N':
                        {
                            states.add(new StateElement(State.SET_AFTER_RANGE,k));
                            break;
                        }
                        default:
                        {
                            states.add(new StateElement(State.SET_AFTER_RANGE,k));
                            k++;
                        }
                    }
                    break;
                }

                case SET_ESCAPE:
                {
                    switch (str.charAt(k))
                    {
                        case 'p':
                        case 'P':
                        {
                            states.add(new StateElement(State.SET_AFTER_SET,k));
                            break;
                        }
                        case'N':
                        {
                            states.add(new StateElement(State.SET_AFTER_LIT,k));
                        }
                        case 's':
                        case 'S':
                        case 'w':
                        case 'W':
                        case 'h':
                        case 'H':
                        case 'd':
                        case 'D':
                        case 'v':
                        case 'V':
                        {
                            states.add(new StateElement(State.SET_AFTER_RANGE,k));
                            k++;
                            break;
                        }
                        default:
                        {
                            states.add(new StateElement(State.SET_AFTER_LIT,k));
                            k++;
                        }
                    }
                    break;
                }

                case SET_FINISH:
                {
                    states.add(new StateElement(State.EXPR_QUANT, k));
                }
            }
        }
        if(stack.size() > 0)
        {
            return new Expression(str, false, k, "EROARE: EXPRESIE INCORECTA LA POZITIA " + k + " - PARANTEZA NEINCHISA");
        }

        if(states.lastElement().state == State.BACKSLASH)
            return new Expression(str, false, k, "EROARE: EXPRESIE INCORECTA LA POZITIA " + k + " - EXPRESIA NU SE POATE TERMINA CU \\");

        final Expression exp = new Expression(str, true, -1, "");
        exp.setTree(states);
        return exp;
    }
}