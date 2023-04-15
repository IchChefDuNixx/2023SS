import time
import sys
import itertools

def animate():
    for i in range(1):
        sys.stdout.write('\r' + 'Loading' + '.' * i)
        sys.stdout.flush()
        time.sleep(0.5)

def spin():
    
    spinner = ['---', '\\\\', '||', '/']
    
    for i in range(len(spinner)):
        sys.stdout.write(spinner[i])
        sys.stdout.flush()
        time.sleep(1)
        if i > 0:
            for _ in range(len(spinner[i])):
                sys.stdout.write('\b')
        i += 1

if __name__ == '__main__':
    animate()
    spin()