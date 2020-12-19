
#diff_list = [1, 3, 1, 1, 1, 3, 1, 1, 3, 1, 3, 3]
diff_list = [1, 1, 1, 1]


def stringify_list(l):
    return ','.join([str(x) for x in l])

def find_chains(stream, pset):
    print(f"entering fn with stream: {stream}")
    stream_length = len(stream)
    for idx in range(0, stream_length-1):
        cur = stream[idx]
        nxt = stream[idx+1]

        print(stringify_list(stream))
        print(" "*(2*idx) + "^ ^")

        if (cur,nxt) == (1,1):
            print("found 1 1")
            new_stream = stream[0:idx] + [2] + stream[idx+2:]
            pset.update({stringify_list(new_stream)})
            find_chains(new_stream, pset)
        elif (cur,nxt) in ( (2,1), (1,2) ):
            print(f"found {cur} {nxt}")
            new_stream = stream[0:idx] + [3] + stream[idx+2:]
            pset.update({stringify_list(new_stream)})
            find_chains(new_stream, pset)

    return pset

diff_list_string = stringify_list(diff_list)
res = find_chains(diff_list, {diff_list_string})
print(f"results ({len(res)})")
for i in res:
    print(i)
