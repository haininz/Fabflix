import sys

if __name__ == '__main__':
#    TextFilePath1 = sys.argv[1]
#    TextFilePath2 = sys.argv[2]

    ts = 0
    tj = 0
    n = 0
    data2 = ""
    with open(sys.argv[1], 'r') as file:
        content = file.readline()
        data = file.read()
        while content:
            content_list = content.rstrip("\n").split()
            temp_ts = content_list[0]
            temp_tj = content_list[1]
            if temp_ts > temp_tj:
                ts += float(content_list[0])
                tj += float(content_list[1])
                n += 1
            content = file.readline()
#             print(float(content_list[0]))
#             print(float(content_list[1]))
#             if float(content_list[0]) > float(content_list[1]):
#                 print("true")
#             else:
#                 print("false")
    if len(sys.argv[2]) > 1:
        with open(sys.argv[2], 'r') as file2:
            content = file2.readline()
            data2 = file2.read()
            while content:
                content_list = content.rstrip("\n").split()
                temp_ts = content_list[0]
                temp_tj = content_list[1]
                if temp_ts > temp_tj:
                    ts += float(content_list[0])
                    tj += float(content_list[1])
                    n += 1
                    content = file2.readline()
            #             print(float(content_list[0]))
        #             print(float(content_list[1]))
        #             if float(content_list[0]) > float(content_list[1]):
        #                 print("true")
        #             else:
        #                 print("false")

    data += "\n"
    data += data2
    with open ('log3.txt', 'w') as fp:
        fp.write(data)

    print("TS: ", (ts / 1000000) / n)
    print("TJ: ", (tj / 1000000) / n)


