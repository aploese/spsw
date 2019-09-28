docker run -it --rm -v $PWD:/workdir -e CROSS_TRIPLE=x86_64-apple-darwin multiarch/crossbuild sh docker-macosx.sh

#docker runs as root so we need to cleanup
sudo chown -R $USERNAME $PWD