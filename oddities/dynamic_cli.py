import time
import sys
import itertools

def animate():
    for i in range(1):
        sys.stdout.write('\r' + 'Loading' + '.' * i)
        sys.stdout.flush()
        time.sleep(0.5)

def spin():
    
    spinner = itertools.cycle(['-', '\\', '|', '/'])
    
    while True:
        sys.stdout.write(next(spinner))
        sys.stdout.flush()
        time.sleep(0.001)
        sys.stdout.write('\b')

if __name__ == '__main__':
    animate()
    spin()