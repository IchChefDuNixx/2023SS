# single perceptron training for 2 linearly separable classes

import numpy as np
import matplotlib.pyplot as plt


# generate random data points with labels for two linearly separable classes and plot it
# numvals = number of samples per class
# returns data in trainIN, size 2*numvals X 3
# each row contains Bias, x, y, i.e., -1 x y
# returns labels (0 and 1) in trainLabels, size 2*numvals
def generate_train_data(numvals):
    # randomly generate 2D training data
    train_in = np.random.normal(0.0, 1.0, size=(numvals, 2))

    # plot data/part 1
    plot_coords = np.reshape(train_in,(2*numvals), 'F')
    plt.scatter(plot_coords[0:numvals], plot_coords[numvals:2*numvals])

    tmp = np.random.normal(5.0, 1.0, size=(numvals, 2))
    train_in = np.concatenate((train_in, tmp), axis=0)

    # plot data/part 2
    plot_coords = np.reshape(tmp, (2*numvals), 'F')
    plt.scatter(plot_coords[0:numvals], plot_coords[numvals:2*numvals])
    # commented out, as the plot is shown at the end after drawing the decision boundary
    # uncomment if you want to see the the plot at this point
    # plt.show()

    # add bias input of -1
    biasweight = np.full((2*numvals, 1), -1.0)
    train_in = np.concatenate((biasweight, train_in), axis=1)

    # generate labels
    trainlabels = np.full(numvals, 0)
    tmp = np.full(numvals, 1)
    trainlabels = np.concatenate((trainlabels, tmp), axis=0)

    return train_in, trainlabels


# compute output of perceptron with step function as activation
def perceptron_out(weights, sample):
    y = np.dot(weights, sample)
    if y > 0:
        y = 1
    else:
        y = 0

    return y


# compute MRSE for evaluation
def compute_mrse(samples, labels, weights):
    mrse = 0.0   # mean root square error
    for s in range(labels.size):
        y = perceptron_out(weights, samples[s])
        # compute residual error
        eps = labels[s] - y
        mrse = mrse + eps*eps
    mrse = np.sqrt(mrse/labels.size)
    return mrse


# plots a line given as an implicit function in line parameter
# line = [a b c] with the line equation ax +by + c = 0
# Xmin/max, Ymin/max define the range of the plot
def plot_line(Xmin, Xmax, Ymin, Ymax, line):
    delta = 0.025
    xrange = np.arange(Xmin, Xmax, delta)
    yrange = np.arange(Ymin, Ymax, delta)
    X, Y = np.meshgrid(xrange,yrange)

    print('Line equation: ', line[0], 'x + ', line[1], 'y + ', line[2], ' = 0')
    # scale line equation such that normal vector has norm 1
    # this is not strictly required, but may avoid issues with the levels threshold of the contour plot
    norm = np.sqrt(line[0]**2 + line[1]**2)
    line = line / norm

    F = np.fabs(line[0]*X + line[1]*Y + line[2])
    plt.contourf(X, Y, (F), levels=[0, 0.01])


def main():
    # learning rate (try changing it!)
    alpha = 0.1

    # init of weights - NEVER use zero for an MLP (it does work with a single perceptron though)
    weights = np.array([0, 0, 0])
    maxiterations = 30  # maximum number of iterations for training

    # generate random data points with labels
    train_in, trainlabels = generate_train_data(1000)

    # perceptron training
    weights_changed = True
    i = 0

    while i < maxiterations and weights_changed is True:
        print("Iteration ", i)
        print("weights ", weights)
        weights_changed = False

        for s in range(trainlabels.size):
            # compute output
            y = perceptron_out(weights, train_in[s])

            # compute residual error
            eps = trainlabels[s] - y

            # update weights
            if eps != 0:
                weights = weights + alpha * eps * train_in[s]
                weights_changed = True

        i = i + 1

    # just a check: after training, the Mean Root Square Error (MRSE) should be zero on training data
    # (try generating test data and evaluate on that!)
    print('MRSE:', compute_mrse(train_in, trainlabels, weights))

    line = np.array([weights[1], weights[2], -weights[0]])
    plot_line(-2, 6, -2, 6, line)
    plt.show()


if __name__ == '__main__':
    main()
