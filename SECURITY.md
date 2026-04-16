# prefer $PREFIX on Termux; otherwise /usr/bin on Linux
ln -sf "$(which python3)" "$HOME/.local/bin/python"
# ensure ~/.local/bin is in PATH (add to shell rc if needed)
