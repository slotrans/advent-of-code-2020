# cribbed from https://ruslanspivak.com/lsbasi-part6/
# adapted to read from a realized list of tokens rather than calling "next" on a lexer,
# and to be embodied in functions rather than class methods


def tokenize_math_string(s):
    # a python string is already a sequence so this does less
    return list(s.replace(" ", ""))


def consume_digit(tokens):
    result = int(tokens[0])
    tokens.pop(0)
    return result


def consume_digit_or_paren_expr(tokens):
    if tokens[0] in ('0','1','2','3','4','5','6','7','8','9'):
        result = int(tokens[0])
        tokens.pop(0)
        return result
    elif tokens[0] == '(':
        tokens.pop(0)
        result = consume_low(tokens)
        tokens.pop(0)
        return result


def consume_high(tokens):
    result = consume_digit_or_paren_expr(tokens)

    while len(tokens) > 0 and tokens[0] == '+':
        tokens.pop(0)
        result = result + consume_digit_or_paren_expr(tokens)

    return result


def consume_low(tokens):
    result = consume_high(tokens)

    while len(tokens) > 0 and tokens[0] == '*':
        tokens.pop(0)
        result = result * consume_high(tokens)

    return result


sample_expressions_p2 = [ 
    {"expr": "2 * 3 + 4 * 5", "expected": 70},
    {"expr": "1 + 2 * 3 + 4 * 5 + 6", "expected": 231},
    {"expr": "1 + (2 * 3) + (4 * (5 + 6))", "expected": 51},
    {"expr": "2 * 3 + (4 * 5)", "expected": 46},
    {"expr": "5 + (8 * 3 + 9 + 3 * 4 * 3)", "expected": 1445},
    {"expr": "5 * 9 * (7 * 3 * 3 + 9 * 3 + (8 + 6 * 4))", "expected": 669060},
    {"expr": "((2 + 4 * 9) * (6 + 9 * 8 + 6) + 6) + 2 + 4 * 2", "expected": 23340},
]

for i in sample_expressions_p2:
    res = consume_low(tokenize_math_string(i["expr"]))
    print("sample expr {} should be {}, got {}".format(i["expr"], i["expected"], res))
