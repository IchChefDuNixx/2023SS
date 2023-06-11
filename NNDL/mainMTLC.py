# MNIST with PyTorch
# loosely based on https://nextjournal.com/gkoehler/pytorch-mnist

import torch
import torchvision.datasets as datasets
import torchvision.transforms as transforms
import matplotlib.pyplot as plt
import torch.nn as nn
import torch.nn.functional as F
import torch.optim as optim
import time
from dataclasses import dataclass, field
from typing import List

# parameters
@dataclass
class HyperParams:
    n_epochs: int = 5
    batch_size_train: int = 64
    batch_size_test: int = 1000
    learning_rate: float = 0.01
    momentum: float = 0.5
    log_interval: int = 10

# used for plotting losses over time
# used later on for visualization of training
@dataclass
class LossPlotData:
    val_counter:  List[int] = field(default_factory=list)
    val_losses: List[float] = field(default_factory=list)
    train_losses: List[float] = field(default_factory=list)
    train_counter: List[int] = field(default_factory=list)


# the network structure
class Net(nn.Module):
    # in the init section we define only what the single layers look like
    # we do not define their connections and how they interact, the ordering is arbitrary
    def __init__(self):
        super(Net, self).__init__()
        # image size: 28x28
        # convolution operation: 1 input channel, 10 output channels, i.e. (W-4) x (H-4) x 10 tensor
        # 24x24x10 out
        self.conv1 = nn.Sequential(
            nn.Conv2d(1, 10, kernel_size=(5, 5)),  # default: stride=1, no padding
            nn.ReLU()
            # nn.MaxPool2d(4, 4)  # 24x24 -> 6x6 after pooling
        )

        self.conv2 = nn.Sequential(
            nn.Conv2d(10, 10, kernel_size=(3, 3), stride=1, padding=1),
            nn.ReLU(),
            nn.MaxPool2d(2, 2)
        )

        self.conv3 = nn.Sequential(
            nn.Conv2d(10, 10, kernel_size=(3, 3), stride=1, padding=1),
            nn.ReLU(),
            nn.MaxPool2d(2, 2)
        )

        # this conv will be combined with pooling by factor 4 -> reduces tensor size to 6x6x10 = 360 dim input vector
        # linear transformation: 360 dim input vector to 10 dim output (= number of classes)
        self.fcblock1 = nn.Sequential(
            nn.Flatten(),
            nn.Linear(360,360),
            nn.ReLU(),
            nn.Dropout(),
            nn.Linear(360, 10),
            # softmax output is computed together with loss
        )

        self.conv4 = nn.Sequential(
            nn.Conv2d(3, 20, kernel_size=(5, 5), stride=(1, 1), padding=2),
            nn.ReLU()
        )

        self.conv5 = nn.Sequential(
            nn.Conv2d(20, 20, kernel_size=(3, 3), padding=1),
            nn.ReLU(),
            nn.MaxPool2d(2, 2)
        )

        self.conv6 = nn.Sequential(
            nn.Conv2d(20, 10, kernel_size=(3, 3), padding=1),
            nn.ReLU(),
            nn.MaxPool2d(2, 2)
        )

        self.fcblock2 = nn.Sequential(
            nn.Flatten(),
            nn.Linear(640, 640),
            nn.ReLU(),
            nn.Dropout(),
            nn.Linear(640, 10)
        )

    # forward pass through the network - this is the actual structure
    # def forward(self, x): # MNIST
    #     x = self.conv1(x)
    #     x = self.conv2(x)
    #     x = self.conv3(x)
    #     x = self.fcblock1(x)
    #     return x

    def forward(self, x): # CIFAR10
        x = self.conv4(x)
        x = self.conv5(x)
        x = self.conv6(x)
        x = self.fcblock2(x)
        return x

def train(epoch, network, optimizer, hyper_params, train_loader, lossplotdata):
    criterion = nn.CrossEntropyLoss()  # combines softmax & cross-entropy
    network.train()
    for batch_idx, (data, target) in enumerate(train_loader):
        optimizer.zero_grad()  # set gradients to zero before optimization (grads are accumulated otherwise)
        output = network(data)
        loss = criterion(output, target) # F.nll_loss(output, target)
        loss.backward()
        optimizer.step()
        if batch_idx % hyper_params.log_interval == 0:
            print('Train Epoch: {} [{}/{} ({:.0f}%)]\tLoss: {:.6f}'.format(
                epoch, batch_idx * len(data), len(train_loader.dataset),
                       100. * batch_idx / len(train_loader), loss.item()))
            lossplotdata.train_losses.append(loss.item())
            lossplotdata.train_counter.append(
                 (batch_idx * 64) + ((epoch - 1) * len(train_loader.dataset)))


# valset = True: datasetloader is the validation set (used during training)
# valset = False: datasetloader is the test set (used after training is complete)
def test(datasetloader, network, lossplotdata, valset=True):
    network.eval()
    test_loss = 0
    correct = 0
    with torch.no_grad():
        for data, target in datasetloader:
            output = network(data)
            test_loss += F.cross_entropy(output, target, size_average=False).item()
            pred = output.data.max(1, keepdim=True)[1]
            correct += pred.eq(target.data.view_as(pred)).sum()
    test_loss /= len(datasetloader.dataset)

    if (valset is True):
        lossplotdata.val_losses.append(test_loss)
        print('\nValidation set: Avg. loss: {:.4f}, Accuracy: {}/{} ({:.0f}%)\n'.format(
            test_loss, correct, len(datasetloader.dataset),
            100. * correct / len(datasetloader.dataset)))
    else:
        print('\nTest set: Avg. loss: {:.4f}, Accuracy: {}/{} ({:.0f}%)\n'.format(
            test_loss, correct, len(datasetloader.dataset),
            100. * correct / len(datasetloader.dataset)))


# main
def main():
    # disable randomization for easier testing
    # comment out the following lines for random init
    random_seed = 1
    torch.backends.cudnn.enabled = True
    # torch.manual_seed(random_seed)
    device = torch.device("cuda:0" if torch.cuda.is_available() else 'cpu')
    print(device)

    # want to use multi-threading? -> set to True
    multi_thread = True
    if (multi_thread is True):
        usenumthreads = torch.get_num_threads()
        print('#Threads used: ', usenumthreads)
    else:
        usenumthreads = 0
        print('#Threads used: ', 1)

    hyper_params = HyperParams()
    lossplotdata = LossPlotData()

    # normalize images
    # 0.1307 is the mean of all MNIST images
    # 0.3081 is the std deviation of all MNIST images
    # there is only a single channel (you can pass the means/stddevs for each channel separated by comma
    # transform1 = transforms.Compose([transforms.ToTensor(), transforms.Normalize(mean=(0.1307,), std=(0.3081,)), ])
    transform2 = transforms.Compose([transforms.ToTensor(), transforms.Normalize((0.5, 0.5, 0.5), (0.5, 0.5, 0.5))])

    # load training data set
    # mnist_trainset = datasets.MNIST(root='./data', train=True, download=True, transform=transform1)
    cifar10_trainset = datasets.CIFAR10(root="./data", train=True, download=True, transform=transform2)

    # split into training and validation set
    # done deterministically here; you can replace this by random_split() if you like
    # mnist_valset = torch.utils.data.Subset(mnist_trainset, range(50000, 60000))
    # mnist_trainset = torch.utils.data.Subset(mnist_trainset, range(0, 50000))

    # mnist_trainset, mnist_valset = torch.utils.data.random_split(mnist_trainset, [50000, 10000], torch.Generator())

    # train_loader = torch.utils.data.DataLoader(mnist_trainset, batch_size=hyper_params.batch_size_train, shuffle=True,
    #                                            num_workers=usenumthreads)
    # val_loader = torch.utils.data.DataLoader(mnist_valset, batch_size=hyper_params.batch_size_test, shuffle=True,
    #                                          num_workers=usenumthreads)

    cifar10_trainset, cifar10_valset = torch.utils.data.random_split(cifar10_trainset, [40000, 10000], torch.Generator())

    train_loader = torch.utils.data.DataLoader(cifar10_trainset, batch_size=hyper_params.batch_size_train, shuffle=True,
                                               num_workers=usenumthreads)
    val_loader = torch.utils.data.DataLoader(cifar10_valset, batch_size=hyper_params.batch_size_test, shuffle=True,
                                             num_workers=usenumthreads)

    # load test data set
    # mnist_testset = datasets.MNIST(root='./data', train=False, download=True, transform=transform1)
    # test_loader = torch.utils.data.DataLoader(mnist_testset, batch_size=hyper_params.batch_size_test, shuffle=True)

    cifar10_testset = datasets.CIFAR10(root="./data", train=False, download=True, transform=transform2)
    test_loader = torch.utils.data.DataLoader(cifar10_testset, batch_size=hyper_params.batch_size_test, shuffle=True)

    # show some example images
    examples = enumerate(test_loader)
    batch_idx, (example_data, example_targets) = next(examples)

    plt.figure()
    for i in range(6):
        plt.subplot(2, 3, i + 1)
        plt.tight_layout()
        plt.imshow(example_data[i][0], cmap='gray', interpolation='none')
        plt.title("Ground Truth: {}".format(example_targets[i]))
        plt.xticks([])
        plt.yticks([])
    plt.show()

    # set up neural network and optimizer
    network = Net()
    optimizer = optim.SGD(network.parameters(), lr=hyper_params.learning_rate, momentum=hyper_params.momentum)

    # training
    starttime = time.time()
    test(val_loader, network, lossplotdata)  # initial evaluation on validation set before any training
    for epoch in range(1, hyper_params.n_epochs + 1):
        train(epoch, network, optimizer, hyper_params, train_loader, lossplotdata)  # training
        test(val_loader, network, lossplotdata)  # evaluate on validation set
        torch.save(network.state_dict(), './model.pth')  # save current network in case anything happens
        torch.save(optimizer.state_dict(), './optimizer.pth')  # save current optimizer state in case anything happens

    print('Evaluation on Test set')
    test(test_loader, network, lossplotdata, valset=False)
    endtime = time.time()
    print('Computation time: ', endtime - starttime)

    # loss plot
    lossplotdata.val_counter = [i * len(train_loader.dataset) for i in range(hyper_params.n_epochs + 1)]

    plt.figure()
    plt.plot(lossplotdata.train_counter, lossplotdata.train_losses, color='blue')
    plt.plot(lossplotdata.val_counter, lossplotdata.val_losses, color='red')
    plt.legend(['Train Loss', 'Valid Loss'], loc='upper right')
    plt.xlabel('number of training examples seen')
    plt.ylabel('log loss')
    plt.show()


if __name__ == '__main__':
    main()
