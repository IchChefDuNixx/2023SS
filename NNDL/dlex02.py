# %% imports

# %% main
# 1

input = [-2, 5, -4]
input2 = [4,3,2,1]
def simple_function(x,y,z):
    q = x+y
    f = q*z
    print("f(x) =", f)

def simple_backpropagation(x,y,z):
    # c
    print("df/dq: 1*z =", z)
    print("df/dz: q*1 =", x+y)
    print("dq/dx: 1+y =", 1)
    print("dq/dy: x+1 =", 1)
    print("df/dx: df/dq * dq/dx =", z*(1))
    print("df/dy: df/dq * dq/dy =", z*(1))
    # df/dq = z
    # dq/dx * z
    # dq/dy * z
    # d
    print("f = (x+y)*z = xz+yz")
    print("df/dx: z =", z)
    print("df/dy: z =", z)
    print("df/dz: x+y =", x+y)


def slightly_more_complex_function(w,x,y,z):
    q = w+x
    r = y+z
    f = q*r
    print("f(x) = q*r =", f)

def slightly_more_complex_backpropagation(w,x,y,z):
    # c
    print("df/dq: r =", y+z)
    print("df/dr: q =", w+x)
    print("dq/dw: 0 =", 0)
    print("dq/dx: 0 =", 0)
    print("dr/dy: 0 =", 0)
    print("dr/dz: 0 =", 0)
    # d
    print("f = (w+x)*(y+z) = w*y + w*z + x*y + x*z")
    print("df/dw: y+z =", y+z)
    print("df/dx: y+z =", y+z)
    print("df/dy: w+x =", w+x)
    print("df/dz: w+x =", w+x)

# simple_function(*input)
# simple_backpropagation(*input)
slightly_more_complex_function(*input2)
slightly_more_complex_backpropagation(*input2)

