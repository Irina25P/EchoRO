import torch

# 1. Vede PyTorch CUDA (NVIDIA)?
print("CUDA Available:", torch.cuda.is_available())

# 2. Vede PyTorch MPS (Apple Silicon - M1/M2/M3)?
print("MPS Available:", torch.backends.mps.is_available())

# 3. Ce versiune de PyTorch ai instalat?
print("PyTorch Version:", torch.__version__)