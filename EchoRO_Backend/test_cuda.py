import torch

print("CUDA Available:", torch.cuda.is_available())

print("MPS Available:", torch.backends.mps.is_available())

print("PyTorch Version:", torch.__version__)